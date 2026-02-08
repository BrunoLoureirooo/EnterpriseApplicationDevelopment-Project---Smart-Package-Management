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
      <h1 class="text-4xl font-bold text-center flex-grow">Editar Conta Sensor</h1>
    </div>

    <!-- Formulário de Edição -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Edite as informações do sensor</h2>

      <form @submit.prevent="updateSensor">
        <div class="mb-4">
          <label class="text-lg font-medium">ID (Nome de Utilizador):</label>
          <input
              v-model="sensor.username"
              type="text"
              required
              readonly
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Nome:</label>
          <input
              v-model="sensor.nome"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Email:</label>
          <input
              v-model="sensor.email"
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
import api from "@/api/api.js"; // Importando a API para enviar os dados

export default {
  data() {
    return {
      sensor: {
        username: "",  // ID (Nome de utilizador do sensor)
        nome: "",      // Nome do sensor
        email: "",     // Email do sensor
      },
    };
  },
  async created() {
    // Pega o parâmetro "username" da URL para buscar o sensor
    const username = this.$route.params.id;
    await this.fetchSensor(username); // Busca os dados do sensor
  },
  methods: {
    // Método para pegar os dados do sensor
    async fetchSensor(username) {
      try {
        const response = await api.get(`/sensorAuth/${username}`); // Chama o endpoint para pegar o sensor pelo ID
        if (response.status === 200) {
          this.sensor = response.data; // Preenche o formulário com os dados do sensor
        } else {
          alert("Erro ao carregar os dados do sensor.");
        }
      } catch (error) {
        console.error("Erro ao carregar os dados do sensor:", error);
        alert("Erro ao tentar carregar os dados do sensor.");
      }
    },

    // Método para atualizar o sensor
    async updateSensor() {
      try {
        const response = await api.patch(`/sensorAuth/${this.sensor.username}`, this.sensor); // Envia os dados atualizados

        if (response.status === 200) {
          alert("Sensor atualizado com sucesso!");
          this.$router.back();  // Redireciona para a lista de sensores
        } else {
          alert("Erro ao atualizar o sensor.");
        }
      } catch (error) {
        console.error("Erro ao atualizar o sensor:", error);
        alert("Erro ao tentar atualizar o sensor.");
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
