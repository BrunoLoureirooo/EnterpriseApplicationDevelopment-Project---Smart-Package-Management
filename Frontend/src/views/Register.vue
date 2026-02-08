<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <!-- Cabeçalho -->
    <div class="flex items-center mb-8">
      <h1 class="text-4xl font-bold text-center flex-grow">Registrar Novo Utilizador</h1>
    </div>

    <!-- Formulário de Registro -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Crie uma nova conta</h2>

      <form @submit.prevent="registerUser">
        <div class="mb-4">
          <label class="text-lg font-medium">Nome de Utilizador:</label>
          <input
              v-model="user.username"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Nome Completo:</label>
          <input
              v-model="user.nome"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Email:</label>
          <input
              v-model="user.email"
              type="email"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Senha:</label>
          <input
              v-model="user.password"
              type="password"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Confirmar Senha:</label>
          <input
              v-model="user.confirmPassword"
              type="password"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mt-6 flex justify-end">
          <button
              type="submit"
              class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
          >
            Registrar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  data() {
    return {
      user: {
        username: "",
        nome: "",
        email: "",
        password: "",
        confirmPassword: "",
      }, // Dados do Utilizador
    };
  },
  methods: {
    // Método para registrar o Utilizador
    async registerUser() {
      // Verificar se as senhas são iguais
      if (this.user.password !== this.user.confirmPassword) {
        alert("As senhas não coincidem.");
        return;
      }

      try {
        delete this.user.confirmPassword; // Remove o campo de confirmação de senha
        const response = await api.post("/cliente", this.user);

        if (response.status === 201) {
          alert("Utilizador registrado com sucesso!");
          // Verificar se o Utilizador está logado e é um gestor
          const authStore = useAuthStore();
          if (authStore.isLoggedIn && authStore.user.role === 'Gestor') {
            this.$router.back(); // Volta para a página anterior se for gestor
          } else {
            this.$router.push("/login"); // Redireciona para o login caso contrário
          }
        } else {
          alert("Erro ao registrar o Utilizador.");
        }
      } catch (error) {
        console.error("Erro ao registrar o utilizador:", error);
        alert("Erro ao tentar registrar o utilizador.");
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
