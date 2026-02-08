<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <div class="flex items-center mb-8">
      <!-- Voltar -->
      <button
          @click="$router.back()"
          class="px-4 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
      >
        Voltar
      </button>
      <h1 class="text-4xl font-bold text-center flex-grow">Editar Encomenda</h1>
    </div>

    <div v-if="encomenda" class="bg-white rounded-lg shadow-md p-6">
      <!-- Formulário de Edição -->
      <div class="mb-4">
        <label for="estado" class="block text-gray-700">Estado</label>
        <select
            id="estado"
            v-model="encomenda.estado"
            class="w-full p-2 border border-gray-300 rounded"
        >
          <option value="PENDENTE">Pendente</option>
          <option value="ENTREGUE">Entregue</option>
          <option value="CANCELADA">Cancelada</option>
        </select>
      </div>

      <div class="mb-4">
        <label for="morada" class="block text-gray-700">Morada de Envio</label>
        <input
            type="text"
            id="morada"
            v-model="encomenda.morada"
            placeholder="Insira a nova morada"
            class="w-full p-2 border border-gray-300 rounded"
        />
      </div>

      <button
          @click="atualizarEncomenda"
          class="w-full px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
      >
        Atualizar Encomenda
      </button>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  data() {
    return {
      encomenda: null,
    };
  },
  async created() {
    try {
      const authStore = useAuthStore();
      await authStore.getToken();

      // Verificar se o Utilizador tem permissão para editar
      if (!authStore.isLoggedIn || (authStore.user.role !== 'Gestor' && authStore.user.role !== 'Funcionario')) {
        this.$router.push('/login'); // Redirecionar para login se não autorizado
        return;
      }

      const encomendaId = this.$route.params.codigo; // Acessa o parâmetro da rota
      const response = await api.get(`/encomenda/${encomendaId}`);
      this.encomenda = response.data;
    } catch (error) {
      console.error("Erro ao carregar encomenda:", error);
    }
  },
  methods: {
    async atualizarEncomenda() {
      try {
        const encomendaId = this.$route.params.codigo;
        const response = await api.patch(`/encomenda/${encomendaId}`, this.encomenda);

        // Atualizar a encomenda após sucesso
        if (response.status === 200) {
          this.$router.push('/encomendas'); // Redireciona de volta para a lista de encomendas
        }
      } catch (error) {
        console.error("Erro ao atualizar encomenda:", error);
      }
    },
  },
};
</script>
