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
        Editar Volume #{{ volumeId }}
      </h1>
    </div>

    <!-- Formulário para editar o volume -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <form @submit.prevent="editVolume">
        <div class="mb-4">
          <label for="estado" class="block text-lg font-medium">Estado</label>
          <select v-model="volume.estado" id="estado" class="w-full mt-2 p-2 border rounded">
            <option value="ENTREGUE">Entregue</option>
            <option value="CANCELADO">Cancelado</option>
            <option value="ATIVO">Ativo</option>
          </select>
        </div>

        <button
            type="submit"
            class="px-4 py-2 bg-green-500 text-white font-semibold rounded hover:bg-green-600"
        >
          Salvar Alterações
        </button>
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
      volume: {
        id: null,
        estado: "",
      },
      volumeId: null,
    };
  },
  async created() {
    try {
      const authStore = useAuthStore();
      // Certifique-se de que o Utilizador está logado
      await authStore.getToken();
      if (!authStore.isLoggedIn) {
        throw new Error("Utilizador não autenticado.");
      }

      // Obter o ID do volume da rota
      this.volumeId = this.$route.params.id;

      // Buscar os dados do volume
      const response = await api.get(`/volume/${this.volumeId}`);
      if (response.data) {
        this.volume = response.data;
      } else {
        console.error("Volume não encontrado");
      }
    } catch (error) {
      console.error("Erro ao buscar dados do volume:", error);
    }
  },
  methods: {
    async editVolume() {
      try {
        // Atualizar volume usando a API
        const response = await api.patch(`/volume/${this.volumeId}`, {
          estado: this.volume.estado,
        });

        if (response.data) {
          // Redirecionar para a página de detalhes do volume
          this.$router.back();
        }
      } catch (error) {
        console.error("Erro ao editar volume:", error);
      }
    },
  },
};
</script>
