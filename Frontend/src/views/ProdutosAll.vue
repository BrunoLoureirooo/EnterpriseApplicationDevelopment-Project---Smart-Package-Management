<template>
  <div class="min-h-screen bg-gray-50 p-8">
    <div class="flex items-center mb-8">
      <h1 class="text-4xl font-bold text-center flex-grow text-gray-800">Lista de Produtos</h1>
    </div>

    <!-- Barra de pesquisa -->
    <div class="mb-6 flex justify-between items-center">
      <div class="w-full md:w-1/3">
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Pesquisar produtos..."
            class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
        />
      </div>
      <div v-if="authStore.user.role === 'Gestor'" class="m-2 ml-4">
        <button
            @click="goToCreateProductPage"
            class="px-4 py-2 bg-green-500 text-white font-semibold rounded-lg hover:bg-green-600 transition duration-300 ease-in-out">
          Criar Novo Produto
        </button>
      </div>
    </div>

    <!-- Lista de Produtos -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
      <div
          v-for="(produto, index) in filteredProducts"
          :key="index"
          class="bg-white rounded-lg shadow-lg hover:shadow-2xl transition-all p-6"
      >
        <h2 class="text-2xl font-bold text-gray-800 mb-4">Produto ID: {{ produto.id }}</h2>
        <p class="text-lg font-medium text-gray-700">Nome: {{ produto.nome }}</p>
        <p class="text-lg font-medium text-gray-700">Descrição: {{ produto.descricao }}</p>

        <!-- Ações do Gestor -->
        <div v-if="authStore.user.role === 'Gestor'" class="mt-6 flex justify-between">
          <button
              @click="goToEditProductPage(produto.id)"
              class="m-2 px-4 py-2 bg-yellow-500 text-white font-semibold rounded-lg hover:bg-yellow-600 transition duration-300 ease-in-out w-full">
            Editar Produto
          </button>
          <button
              @click="deleteProduct(produto.id)"
              class="m-2 px-4 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition duration-300 ease-in-out w-full">
            Eliminar Produto
          </button>
        </div>
      </div>

      <!-- Caso não haja produtos -->
      <div v-if="filteredProducts.length === 0" class="bg-white rounded-lg shadow-lg p-6 text-center">
        <p class="text-lg font-medium text-gray-500">Nenhum produto encontrado.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    // Verificar se o Utilizador está autenticado
    if (!authStore.isLoggedIn) {
      alert("Você precisa estar logado para acessar esta página.");
      router.push('/login'); // Redirecionar para a página de login
    }

    // Variáveis reativas
    const produtos = ref([]);
    const loading = ref(false);
    const searchQuery = ref("");

    // Função para obter produtos
    const fetchProducts = async () => {
      try {
        loading.value = true;
        const response = await api.get('/produto/all'); // Chamada à API para obter produtos
        if (response.data && Array.isArray(response.data)) {
          produtos.value = response.data;
        } else {
          produtos.value = [];
        }
      } catch (error) {
        console.error("Erro ao obter produtos:", error.message);
      } finally {
        loading.value = false;
      }
    };

    // Função para filtrar produtos com base na pesquisa
    const filteredProducts = computed(() => {
      if (!searchQuery.value) {
        return produtos.value;
      }
      return produtos.value.filter(produto =>
          produto.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          produto.descricao.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    });

    // Funções de navegação
    const goToCreateProductPage = () => {
      router.push('/produtos/criar'); // Página para criar um novo produto
    };

    const goToEditProductPage = (produtoId) => {
      router.push(`/produtos/editar/${produtoId}`); // Página para editar o produto
    };

    // Função para excluir produto
    const deleteProduct = async (produtoId) => {
      if (confirm("Tem certeza de que deseja eliminar este produto?")) {
        try {
          loading.value = true;
          await api.delete(`/produto/${produtoId}`); // Chamada à API para excluir produto
          alert("Produto eliminado com sucesso!");
          // Recarregar a lista de produtos após a eliminação
          fetchProducts();
        } catch (error) {
          console.error("Erro ao excluir produto:", error.message);
        } finally {
          loading.value = false;
        }
      }
    };

    // Carregar os produtos ao montar o componente
    onMounted(() => {
      fetchProducts();
    });

    // Retorna os dados para o template
    return {
      authStore,
      produtos,
      searchQuery,
      loading,
      goToCreateProductPage,
      goToEditProductPage,
      deleteProduct,
      filteredProducts,
    };
  },
};
</script>
