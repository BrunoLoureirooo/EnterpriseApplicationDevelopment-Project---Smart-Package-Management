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
      <h1 class="text-4xl font-bold text-center flex-grow">Editar Cliente</h1>
    </div>

    <!-- Formulário de Edição -->
    <div v-if="cliente" class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Edite as informações do cliente</h2>

      <form @submit.prevent="updateCliente">
        <div class="mb-4">
          <label class="text-lg font-medium">Nome de Utilizador:</label>
          <input
              v-model="cliente.username"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Nome Completo:</label>
          <input
              v-model="cliente.nome"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Email:</label>
          <input
              v-model="cliente.email"
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
            Atualizar Cliente
          </button>
        </div>
      </form>
    </div>

    <!-- Loading Spinner -->
    <div v-else class="flex justify-center items-center">
      <div class="spinner-border text-blue-500" role="status">
        <span class="sr-only">Carregando...</span>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router';
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  data() {
    return {
      cliente: null, // Dados do cliente
    };
  },
  async created() {
    // Verificar se o Utilizador está logado como gestor
    const authStore = useAuthStore();
    if (!authStore.isLoggedIn || authStore.user.role !== 'Gestor') {
      this.$router.push('/login'); // Redireciona se não for gestor
    } else {
      // Carregar dados do cliente ao criar o componente
      this.loadCliente();
    }
  },
  methods: {
    // Método para carregar os dados do cliente
    async loadCliente() {
      const route = useRoute();
      const clienteId = route.params.id;

      try {
        const response = await api.get(`/cliente/${clienteId}`);
        if (response.status === 200) {
          this.cliente = response.data;
        } else {
          alert("Cliente não encontrado.");
          this.$router.push("/clientes");
        }
      } catch (error) {
        console.error("Erro ao carregar os dados do cliente:", error);
        alert("Erro ao carregar os dados do cliente.");
        this.$router.push("/clientes");
      }
    },

    // Método para atualizar os dados do cliente
    async updateCliente() {
      try {
        const response = await api.patch(`/cliente/${this.cliente.id}`, this.cliente);

        if (response.status === 200) {
          alert("Cliente atualizado com sucesso!");
          this.$router.push("/clientes"); // Redireciona para a lista de clientes
        } else {
          alert("Erro ao atualizar o cliente.");
        }
      } catch (error) {
        console.error("Erro ao atualizar o cliente:", error);
        alert("Erro ao tentar atualizar o cliente.");
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
