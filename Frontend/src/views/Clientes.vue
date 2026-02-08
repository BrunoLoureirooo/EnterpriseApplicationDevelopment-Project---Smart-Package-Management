<template>
  <div class="min-h-screen bg-gray-50 p-8">
    <div class="flex items-center mb-8">
      <h1 class="text-4xl font-bold text-center flex-grow text-gray-800">Lista de Clientes</h1>
    </div>

    <!-- Barra de pesquisa -->
    <div class="mb-6 flex justify-between items-center">
      <div class="w-full md:w-1/3">
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Pesquisar por email, nome ou username..."
            class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
        />
      </div>
      <div v-if="authStore.user.role === 'Gestor'" class="m-2 ml-4">
        <button
            @click="goToCreateClientPage"
            class="px-4 py-2 bg-green-500 text-white font-semibold rounded-lg hover:bg-green-600 transition duration-300 ease-in-out">
          Criar Novo Cliente
        </button>
      </div>
    </div>

    <!-- Lista de Clientes -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
      <div
          v-for="(cliente, index) in filteredClients"
          :key="index"
          class="bg-white rounded-lg shadow-lg hover:shadow-2xl transition-all p-6"
      >
        <h2 class="text-2xl font-bold text-gray-800 mb-4">Cliente: {{ cliente.nome }}</h2>
        <p class="text-lg font-medium text-gray-700">Username: {{ cliente.username }}</p>
        <p class="text-lg font-medium text-gray-700">Email: {{ cliente.email }}</p>

        <!-- Ações do Gestor -->
        <div v-if="authStore.user.role === 'Gestor'" class="mt-6 flex justify-between">
          <button
              @click="goToEditClientPage(cliente.username)"
              class="m-2 px-4 py-2 bg-yellow-500 text-white font-semibold rounded-lg hover:bg-yellow-600 transition duration-300 ease-in-out w-full">
            Editar Cliente
          </button>
          <button
              @click="deleteClient(cliente.username)"
              class="m-2 px-4 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition duration-300 ease-in-out w-full">
            Eliminar Cliente
          </button>
        </div>
      </div>

      <!-- Caso não haja clientes -->
      <div v-if="filteredClients.length === 0" class="bg-white rounded-lg shadow-lg p-6 text-center">
        <p class="text-lg font-medium text-gray-500">Nenhum cliente encontrado.</p>
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

    // Verificar se o Utilizador está logado e é Gestor
    if (!authStore.isLoggedIn || authStore.user.role !== 'Gestor') {
      alert("Você precisa estar logado como Gestor para acessar esta página.");
      router.push('/login');
    }

    // Variáveis reativas
    const clientes = ref([]);
    const loading = ref(false);
    const searchQuery = ref("");

    // Função para obter clientes
    const fetchClients = async () => {
      try {
        loading.value = true;
        const response = await api.get('/cliente/all'); // Chamada à API para obter clientes
        if (response.data && Array.isArray(response.data)) {
          clientes.value = response.data;
        } else {
          clientes.value = [];
        }
      } catch (error) {
        console.error("Erro ao obter clientes:", error.message);
      } finally {
        loading.value = false;
      }
    };

    // Função para filtrar clientes com base na pesquisa
    const filteredClients = computed(() => {
      if (!searchQuery.value) {
        return clientes.value;
      }
      return clientes.value.filter(cliente =>
          cliente.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          cliente.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          cliente.username.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    });

    // Funções de navegação
    const goToCreateClientPage = () => {
      router.push('/register'); // Página para criar um novo cliente
    };

    const goToEditClientPage = (clienteUsername) => {
      router.push(`/clientes/editar/${clienteUsername}`); // Página para editar o cliente
    };

    // Função para excluir cliente
    const deleteClient = async (clienteUsername) => {
      if (confirm("Tem certeza de que deseja eliminar este cliente?")) {
        try {
          loading.value = true;
          await api.delete(`/cliente/${clienteUsername}`); // Chamada à API para excluir cliente
          alert("Cliente eliminado com sucesso!");
          // Recarregar a lista de clientes após a eliminação
          fetchClients();
        } catch (error) {
          console.error("Erro ao excluir cliente:", error.message);
        } finally {
          loading.value = false;
        }
      }
    };

    // Carregar os clientes ao montar o componente
    onMounted(() => {
      fetchClients();
    });

    // Retorna os dados para o template
    return {
      authStore,
      clientes,
      searchQuery,
      loading,
      goToCreateClientPage,
      goToEditClientPage,
      deleteClient,
      filteredClients,
    };
  },
};
</script>
