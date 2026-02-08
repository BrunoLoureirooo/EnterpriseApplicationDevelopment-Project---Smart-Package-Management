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
      <h1 class="text-4xl font-bold text-center flex-grow">Lista de Sensores</h1>
    </div>

    <!-- Lista de Sensores -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="mb-4 flex justify-end">
        <button
            @click="$router.push('/sensoresAuth/criar')"
            class="px-6 py-2 bg-green-500 text-white font-semibold rounded hover:bg-green-600"
        >
          Adicionar Sensor
        </button>
      </div>
      <table class="min-w-full">
        <thead>
        <tr>
          <th class="px-6 py-4 text-left">ID</th>
          <th class="px-6 py-4 text-left">Nome</th>
          <th class="px-6 py-4 text-left">Email</th>
          <th class="px-6 py-4 text-left">Ações</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="sensor in sensores" :key="sensor.username">
          <td class="px-6 py-4">{{ sensor.username }}</td>
          <td class="px-6 py-4">{{ sensor.nome }}</td>
          <td class="px-6 py-4">{{ sensor.email }}</td>
          <td class="px-6 py-4">
            <button
                @click="$router.push(`/sensoresAuth/editar/${sensor.username}`)"
                class="px-4 py-2 bg-yellow-500 text-white font-semibold rounded hover:bg-yellow-600 mr-2"
            >
              Editar
            </button>
            <button
                @click="deleteSensor(sensor.username)"
                class="px-4 py-2 bg-red-500 text-white font-semibold rounded hover:bg-red-600"
            >
              Apagar
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";

export default {
  data() {
    return {
      sensores: [], // Lista de sensores
    };
  },
  async created() {
    await this.fetchSensores();
  },
  methods: {
    async fetchSensores() {
      try {
        const response = await api.get("/sensorAuth/all"); // Verifique o endpoint
        if (response.status === 200) {
          this.sensores = response.data; // Atribui os dados retornados à lista de sensores
        } else {
          alert("Erro ao carregar os sensores.");
        }
      } catch (error) {
        console.error("Erro ao carregar os sensores:", error);
        alert("Erro ao tentar carregar os sensores.");
      }
    },
    async deleteSensor(username) {
      if (confirm("Tem a certeza que deseja apagar este sensor?")) {
        try {
          const response = await api.delete(`/sensorAuth/${username}`); // Verifique o endpoint
          if (response.status === 200) {
            alert("Sensor apagado com sucesso.");
            await this.fetchSensores(); // Recarrega a lista de sensores
          } else {
            alert("Erro ao apagar o sensor.");
          }
        } catch (error) {
          console.error("Erro ao apagar o sensor:", error);
          alert("Erro ao tentar apagar o sensor.");
        }
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
