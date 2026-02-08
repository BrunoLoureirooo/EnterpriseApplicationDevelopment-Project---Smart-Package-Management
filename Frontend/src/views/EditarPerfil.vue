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
      <h1 class="text-4xl font-bold text-center flex-grow">Editar Perfil</h1>
    </div>

    <!-- Formulário de Edição -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Atualize suas informações</h2>

      <form @submit.prevent="updateProfile">
        <div class="mb-4">
          <label class="text-lg font-medium">Nome de Utilizador:</label>
          <input
              v-model="user.username"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              readonly
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Nome Completo:</label>
          <input
              v-model="user.nome"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Email:</label>
          <input
              v-model="user.email"
              type="email"
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div class="mt-6 flex justify-end">
          <button
              type="submit"
              class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
          >
            Salvar Alterações
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from "@/stores/auth.js";
import api from "@/api/api.js";

export default {
  data() {
    return {
      user: {}, // Dados do Utilizador
    };
  },
  async created() {
    const authStore = useAuthStore();
    await authStore.getToken();
    if (authStore.user) {
      this.user = {...authStore.user}; // Cria uma cópia dos dados do Utilizador para edição
      //remove role
      delete this.user.role;
    } else {
      console.error("Utilizador não encontrado!");
    }
  },
  methods: {
    // Método para atualizar o perfil
    async updateProfile() {
      try {
        const response = await api.patch(`/cliente/${this.user.username}`, this.user);

        if (response.status === 200) {
          alert("Perfil atualizado com sucesso!");
          // Após atualizar, redireciona de volta para a página de perfil
          this.$router.push("/perfil");
        } else {
          alert("Erro ao atualizar o perfil.");
        }
      } catch (error) {
        console.error("Erro ao atualizar o perfil:", error);
        alert("Erro ao tentar atualizar o perfil.");
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
