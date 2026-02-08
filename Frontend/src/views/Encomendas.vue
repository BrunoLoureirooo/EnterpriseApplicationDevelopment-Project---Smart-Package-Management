<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <div class="flex items-center mb-8">
      <!-- Back button aligned to the left -->
      <button
          @click="$router.back()"
          class="px-4 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
      >
        Voltar
      </button>
      <h1 class="text-4xl font-bold text-center flex-grow">
        Lista de Encomendas
      </h1>
    </div>

    <!-- Barra de pesquisa -->
    <div class="mb-6">
      <input
          v-model="searchQuery"
          type="text"
          placeholder="Pesquisar encomendas..."
          class="px-4 py-2 w-full md:w-1/3 border rounded"
      />
    </div>

    <!-- Lista de Encomendas -->
    <div class="grid grid-cols-1 gap-8">
      <div
          v-for="(encomenda, index) in filteredEncomendas"
          :key="index"
          class="bg-white rounded-lg shadow-md p-6"
      >
        <!-- Encomenda Information -->
        <a :href="'/volumesEncomendas/' + encomenda.codigo">
          <h2 class="text-2xl font-bold text-black mb-4">Encomenda #{{ encomenda.codigo }}</h2>
          <p class="text-lg font-medium">Estado: {{ encomenda.estado }}</p>
          <p class="text-lg font-medium">Morada de Envio: {{ encomenda.morada }}</p>
          <p v-if="authStore.user.role !== 'Cliente'" class="text-lg font-medium">
            Cliente: {{ encomenda.clienteUsername }}
          </p>
        </a>

        <!-- Editar button only for 'Gestor' role -->
        <div v-if="authStore.user.role === 'Gestor'" class="mt-4">
          <router-link :to="'/editarEncomenda/' + encomenda.codigo">
            <button
                class="px-4 py-2 bg-yellow-500 text-white font-semibold rounded hover:bg-yellow-600"
            >
              Editar Encomenda
            </button>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";
import { ref, computed } from "vue";

export default {
  data() {
    return {
      encomendas: [],
      searchQuery: "", // Armazenar a consulta de pesquisa
    };
  },
  setup() {
    const authStore = useAuthStore(); // Access the auth store

    return {
      authStore,
    };
  },
  async created() {
    try {
      const authStore = useAuthStore();
      // Corrigir a chamada de getToken para ser assíncrona
      await authStore.getToken();

      // Verificar se está logado
      if (!authStore.isLoggedIn) {
        throw new Error("Utilizador não autenticado.");
      }

      let endPoint;
      if (authStore.user.role === "Cliente") {
        endPoint = "/encomenda/cliente/" + authStore.user.username;
      } else {
        endPoint = "/encomenda/all";
      }
      const response = await api.get(endPoint);
      this.encomendas = response.data;
    } catch (error) {
      console.error("Error fetching encomendas:", error);
    }
  },

  computed: {
    filteredEncomendas() {
      // Filtrar encomendas com base na pesquisa
      if (!this.searchQuery) {
        return this.encomendas;
      }
      return this.encomendas.filter(encomenda =>
          encomenda.estado.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          encomenda.morada.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          encomenda.clienteUsername.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
  },
};
</script>
