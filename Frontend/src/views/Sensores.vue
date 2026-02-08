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

      <!-- Title centered -->
      <h1 class="text-4xl font-bold text-center flex-grow">
        Detalhes do Sensor #{{ sensorId }}
      </h1>
    </div>

    <!-- Exibir detalhes do Sensor somente após carregamento -->
    <div v-if="sensor" class="grid grid-cols-1 gap-8">
      <div
          class="bg-white rounded-lg shadow-md p-6"
      >
        <h2 class="text-2xl font-bold text-black mb-4">
          ID do Sensor: {{ sensor.id }}
        </h2>
        <p class="text-lg font-medium">
          Atividade: {{ sensor.ativo ? 'Ativo' : 'Inativo' }}
        </p>
        <p class="text-lg font-medium">
          <a
              @click.prevent="loadMedicoes"
              class="text-blue-500 underline hover:text-blue-700 cursor-pointer"
          >
            Ver Medições
          </a>
        </p>
        <p class="text-lg font-medium">Tipo: {{ sensor.tipo }}</p>
      </div>

      <!-- Lista de Medições -->
      <div v-if="medicoes.length > 0" class="bg-white rounded-lg shadow-md p-6">
        <h3 class="text-xl font-bold text-black mb-4">Medições:</h3>
        <ul>
          <li v-for="(medicao, index) in medicoes" :key="index" class="text-lg">
            Medição #{{ medicao.id }} Valor: {{ medicao.valor }}
          </li>
        </ul>
      </div>

      <!-- Caso não haja medições -->
      <div v-if="medicoes.length === 0" class="bg-white rounded-lg shadow-md p-6 text-center">
        <p class="text-lg font-medium text-gray-500">Nenhuma medição encontrada para este sensor.</p>
      </div>
    </div>

    <!-- Caso o sensor não exista -->
    <div v-if="!sensor" class="bg-white rounded-lg shadow-md p-6 text-center">
      <p class="text-lg font-medium text-gray-500">Sensor não encontrado.</p>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  data() {
    return {
      sensor: null, // Detalhes do sensor
      sensorId: null, // ID do sensor da URL
      medicoes: [], // Lista de medições do sensor
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

      // Obtém o 'sensorId' dos parâmetros da rota
      this.sensorId = this.$route.params.id;

      // Buscar detalhes do sensor com base no sensorId
      const response = await api.get(`/sensor/${this.sensorId}`);

      // Armazenar os dados do sensor
      if (response.data) {
        this.sensor = response.data;
      } else {
        this.sensor = null;
      }
    } catch (error) {
      console.error("Erro ao buscar o sensor:", error);
      this.sensor = null;
    }
  },
  methods: {
    async loadMedicoes() {
      try {
        // Buscar medições do sensor usando o sensorId
        const response = await api.get(`/sensor/${this.sensorId}/medicoes`);

        // Armazenar as medições
        this.medicoes = response.data;
      } catch (error) {
        console.error("Erro ao buscar medições:", error);
      }
    },
  },
};
</script>
