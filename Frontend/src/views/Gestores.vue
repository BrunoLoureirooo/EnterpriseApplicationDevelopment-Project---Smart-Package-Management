<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <!-- Cabeçalho -->
    <div class="flex items-center mb-8">
      <button
          @click="$router.back()"
          class="px-4 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
      >
        Voltar
      </button>
      <h1 class="text-4xl font-bold text-center flex-grow">Lista de Gestores</h1>
    </div>

    <!-- Barra de Pesquisa -->
    <div class="mb-6 flex justify-between items-center">
      <div class="w-full md:w-1/3">
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Pesquisar por nome, email ou username"
            class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div v-if="authStore.user.role === 'Gestor'" class="m-2 ml-4">
        <button
            @click="goToCreateGestorPage"
            class="px-4 py-2 bg-green-500 text-white font-semibold rounded-lg hover:bg-green-600 transition duration-300 ease-in-out"
        >
          Criar Novo Gestor
        </button>
      </div>
    </div>

    <!-- Lista de Gestores -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
      <div
          v-for="(gestor, index) in filteredGestores"
          :key="index"
          class="bg-white rounded-lg shadow-lg hover:shadow-2xl transition-all p-6"
      >
        <h2 class="text-2xl font-bold text-gray-800 mb-4">ID: {{ gestor.username }}</h2>
        <p class="text-lg font-medium text-gray-700">Nome: {{ gestor.nome }}</p>
        <p class="text-lg font-medium text-gray-700">Email: {{ gestor.email }}</p>

        <!-- Ações do Gestor -->
        <div v-if="authStore.user.role === 'Gestor'" class="mt-6 flex justify-between">
          <button
              @click="goToEditGestorPage(gestor.username)"
              class="m-2 px-4 py-2 bg-yellow-500 text-white font-semibold rounded-lg hover:bg-yellow-600 transition duration-300 ease-in-out w-full"
          >
            Editar
          </button>
          <button
              @click="deleteGestor(gestor.username)"
              class="m-2 px-4 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition duration-300 ease-in-out w-full"
          >
            Eliminar
          </button>
        </div>
      </div>

      <!-- Caso não haja gestores -->
      <div v-if="filteredGestores.length === 0" class="bg-white rounded-lg shadow-lg p-6 text-center">
        <p class="text-lg font-medium text-gray-500">Nenhum gestor encontrado.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const gestores = ref([]);
    const searchQuery = ref("");

    // Verifica se o Utilizador é um gestor, se não, redireciona para login
    if (!authStore.isLoggedIn || authStore.user.role !== "Gestor") {
      router.push("/login");
    }

    // Função para carregar os gestores
    const fetchGestores = async () => {
      try {
        const response = await api.get("/gestor/all"); // Chamada à API para obter os gestores
        if (response.data && Array.isArray(response.data)) {
          gestores.value = response.data;
        } else {
          gestores.value = [];
        }
      } catch (error) {
        console.error("Erro ao obter gestores:", error.message);
      }
    };

    // Função para filtrar gestores com base na pesquisa
    const filteredGestores = computed(() => {
      if (!searchQuery.value) {
        return gestores.value;
      }
      return gestores.value.filter((gestor) =>
          gestor.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          gestor.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          gestor.username.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    });

    // Funções de navegação
    const goToCreateGestorPage = () => {
      router.push("/gestores/criar"); // Página para criar um novo gestor
    };

    const goToEditGestorPage = (gestorId) => {
      router.push(`/gestores/editar/${gestorId}`); // Página para editar o gestor
    };

    // Função para excluir um gestor
    const deleteGestor = async (gestorId) => {
      if (confirm("Tem certeza de que deseja eliminar este gestor?")) {
        try {
          await api.delete(`/gestor/${gestorId}`); // Chamada à API para excluir gestor
          alert("Gestor eliminado com sucesso!");
          fetchGestores(); // Recarregar a lista de gestores após a exclusão
        } catch (error) {
          console.error("Erro ao excluir gestor:", error.message);
        }
      }
    };

    // Carregar os gestores ao montar o componente
    onMounted(() => {
      fetchGestores();
    });

    // Retorna os dados para o template
    return {
      authStore,
      gestores,
      searchQuery,
      goToCreateGestorPage,
      goToEditGestorPage,
      deleteGestor,
      filteredGestores,
    };
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
