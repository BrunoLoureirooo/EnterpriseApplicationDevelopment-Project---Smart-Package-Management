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
      <h1 class="text-4xl font-bold text-center flex-grow">Lista de Funcionários</h1>
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
            @click="goToCreateFuncionarioPage"
            class="px-4 py-2 bg-green-500 text-white font-semibold rounded-lg hover:bg-green-600 transition duration-300 ease-in-out"
        >
          Criar Novo Funcionário
        </button>
      </div>
    </div>

    <!-- Lista de Funcionários -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
      <div
          v-for="(funcionario, index) in filteredFuncionarios"
          :key="index"
          class="bg-white rounded-lg shadow-lg hover:shadow-2xl transition-all p-6"
      >
        <h2 class="text-2xl font-bold text-gray-800 mb-4">ID: {{ funcionario.username }}</h2>
        <p class="text-lg font-medium text-gray-700">Nome: {{ funcionario.nome }}</p>
        <p class="text-lg font-medium text-gray-700">Email: {{ funcionario.email }}</p>

        <!-- Ações do Gestor -->
        <div v-if="authStore.user.role === 'Gestor'" class="mt-6 flex justify-between">
          <button
              @click="goToEditFuncionarioPage(funcionario.username)"
              class="m-2 px-4 py-2 bg-yellow-500 text-white font-semibold rounded-lg hover:bg-yellow-600 transition duration-300 ease-in-out w-full"
          >
            Editar
          </button>
          <button
              @click="deleteFuncionario(funcionario.username)"
              class="m-2 px-4 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition duration-300 ease-in-out w-full"
          >
            Eliminar
          </button>
        </div>
      </div>

      <!-- Caso não haja funcionários -->
      <div v-if="filteredFuncionarios.length === 0" class="bg-white rounded-lg shadow-lg p-6 text-center">
        <p class="text-lg font-medium text-gray-500">Nenhum funcionário encontrado.</p>
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
    const funcionarios = ref([]);
    const searchQuery = ref("");

    // Verifica se o Utilizador é um gestor, se não, redireciona para login
    if (!authStore.isLoggedIn || authStore.user.role !== "Gestor") {
      router.push("/login");
    }

    // Função para carregar os funcionários
    const fetchFuncionarios = async () => {
      try {
        const response = await api.get("/funcionario/all"); // Chamada à API para obter os funcionários
        if (response.data && Array.isArray(response.data)) {
          funcionarios.value = response.data;
        } else {
          funcionarios.value = [];
        }
      } catch (error) {
        console.error("Erro ao obter funcionários:", error.message);
      }
    };

    // Função para filtrar funcionários com base na pesquisa
    const filteredFuncionarios = computed(() => {
      if (!searchQuery.value) {
        return funcionarios.value;
      }
      return funcionarios.value.filter((funcionario) =>
          funcionario.nome.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          funcionario.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          funcionario.username.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    });

    // Funções de navegação
    const goToCreateFuncionarioPage = () => {
      router.push("/funcionarios/criar"); // Página para criar um novo funcionário
    };

    const goToEditFuncionarioPage = (funcionarioId) => {
      router.push(`/funcionarios/editar/${funcionarioId}`); // Página para editar o funcionário
    };

    // Função para excluir um funcionário
    const deleteFuncionario = async (funcionarioId) => {
      if (confirm("Tem certeza de que deseja eliminar este funcionário?")) {
        try {
          await api.delete(`/funcionario/${funcionarioId}`); // Chamada à API para excluir funcionário
          alert("Funcionário eliminado com sucesso!");
          fetchFuncionarios(); // Recarregar a lista de funcionários após a exclusão
        } catch (error) {
          console.error("Erro ao excluir funcionário:", error.message);
        }
      }
    };

    // Carregar os funcionários ao montar o componente
    onMounted(() => {
      fetchFuncionarios();
    });

    // Retorna os dados para o template
    return {
      authStore,
      funcionarios,
      searchQuery,
      goToCreateFuncionarioPage,
      goToEditFuncionarioPage,
      deleteFuncionario,
      filteredFuncionarios,
    };
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
