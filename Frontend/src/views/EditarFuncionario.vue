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
      <h1 class="text-4xl font-bold text-center flex-grow">Editar Funcionário</h1>
    </div>

    <!-- Formulário de Edição -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Edite os dados do funcionário</h2>

      <form @submit.prevent="updateFuncionario">
        <div class="mb-4">
          <label class="text-lg font-medium">Nome de Utilizador (ID do Funcionário):</label>
          <input
              v-model="funcionario.username"
              type="text"
              required
              disabled
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Nome Completo:</label>
          <input
              v-model="funcionario.nome"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Email:</label>
          <input
              v-model="funcionario.email"
              type="email"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div class="mt-6 flex justify-end">
          <button
              type="submit"
              class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
          >
            Atualizar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth.js";

export default {
  data() {
    return {
      funcionario: {
        username: "",
        nome: "",
        email: "",
      }, // Dados do funcionário
    };
  },
  async created() {
    const username = this.$route.params.id; // Pega o username do parâmetro da rota
    await this.fetchFuncionario(username); // Carrega os dados do funcionário
  },
  methods: {
    // Método para buscar os dados do funcionário pelo username
    async fetchFuncionario(username) {
      try {
        const response = await api.get(`/funcionario/${username}`);
        if (response.status === 200) {
          this.funcionario = response.data;
        } else {
          alert("Funcionário não encontrado.");
          this.$router.push("/funcionarios"); // Redireciona para a lista de funcionários caso não encontre
        }
      } catch (error) {
        console.error("Erro ao carregar os dados do funcionário:", error);
        alert("Erro ao tentar carregar os dados do funcionário.");
      }
    },

    // Método para atualizar os dados do funcionário
    async updateFuncionario() {
      try {
        delete this.funcionario.confirmPassword; // Remove o campo de confirmação de senha
        const response = await api.patch(`/funcionario/${this.funcionario.username}`, this.funcionario);

        if (response.status === 200) {
          alert("Funcionário atualizado com sucesso!");
          this.$router.push("/funcionarios"); // Redireciona para a lista de funcionários após a atualização
        } else {
          alert("Erro ao atualizar o funcionário.");
        }
      } catch (error) {
        console.error("Erro ao atualizar o funcionário:", error);
        alert("Erro ao tentar atualizar o funcionário.");
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
