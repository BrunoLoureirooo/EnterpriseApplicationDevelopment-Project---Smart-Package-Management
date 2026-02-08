<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <div class="flex items-center mb-8">
      <button @click="$router.back()" class="px-4 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600">
        Voltar
      </button>
      <h1 class="text-4xl font-bold text-center flex-grow">Lista de Volumes</h1>
    </div>

    <!-- Lista de Volumes -->
    <div class="grid grid-cols-1 gap-8">
      <div v-for="(volume, index) in volumes" :key="index" class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold text-black mb-4">ID do Volume: {{ volume.id }}</h2>
        <p class="text-lg font-medium">Estado: {{ volume.estado }}</p>

        <!-- Produtos -->
        <span class="text-lg font-medium">
          <span>Produtos:</span>
          <ul>
            <li v-for="(produtoId, idx) in volume.produtosIds" :key="idx">
              <button @click="goToProdutoPage(produtoId, volume.id)" class="text-blue-500 hover:underline">
                Produto ID: {{ produtoId }} (Quantidade: {{ volume.produtosQuants[idx] }})
              </button>
            </li>
          </ul>
        </span>

        <!-- Sensores -->
        <span class="text-lg font-medium">
          <span>Sensores:</span>
          <ul>
            <li v-if="volume.sensores && volume.sensores.length === 0" class="text-gray-500">
              Nenhum sensor atribuído a este volume.
            </li>
            <li v-for="(sensor, idx) in volume.sensores" :key="idx">
              <button @click="goToSensorPage(sensor.id)" class="text-blue-500 hover:underline">
                Sensor ID: {{ sensor.id }} - Tipo: {{ sensor.tipo }}
              </button>
            </li>
          </ul>
        </span>

        <!-- Adicionar Sensor (visível apenas para Gestores) -->
        <div v-if="authStore.user.role === 'Gestor' || authStore.user.role === 'Funcionario'" class="mt-4">
          <select v-model="volume.selectedSensorId" class="p-2 border rounded">
            <option value="" disabled selected>Selecione um Sensor</option>
            <option v-for="sensor in availableSensors.filter(s => !volume.sensoresIds.includes(s.id))" :key="sensor.id" :value="sensor.id">
              ID: {{ sensor.id }} - Tipo: {{ sensor.tipo }}
            </option>
          </select>
          <div v-if="volume.selectedSensorId === ''" class="text-red-500 text-sm mt-2">
            Por favor, selecione um sensor.
          </div>

          <button
              @click="addSensorToVolume(volume.id)"
              class="px-4 py-2 bg-green-500 text-white font-semibold rounded hover:bg-green-600 mt-2"
              :disabled="loading || volume.selectedSensorId === ''"
          >
            {{ loading ? 'Adicionando...' : 'Adicionar Sensor' }}
          </button>
        </div>

        <div v-if="authStore.user.role === 'Gestor' || authStore.user.role==='Funcionario'" class="mt-4">
          <button @click="editVolume(volume.id)" class="px-4 py-2 bg-yellow-500 text-white font-semibold rounded hover:bg-yellow-600">
            Editar Volume
          </button>
        </div>
      </div>

      <!-- Caso não haja volumes -->
      <div v-if="volumes.length === 0" class="bg-white rounded-lg shadow-md p-6 text-center">
        <p class="text-lg font-medium text-gray-500">Nenhum volume encontrado para esta encomenda.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';  // Importar useRouter para obter o router
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  setup() {
    const authStore = useAuthStore();
    const route = useRoute();
    const router = useRouter(); // Usando useRouter para acessar o router

    // Variáveis reativas
    const volumes = ref([]);
    const availableSensors = ref([]);
    const loading = ref(false);

    // Função assíncrona para obter volumes e sensores
    const fetchVolumesAndSensors = async () => {
      try {
        await authStore.getToken();  // Garantir que o token seja obtido

        // Verificar se está logado
        if (!authStore.isLoggedIn) {
          throw new Error("Utilizador não autenticado.");
        }


        // Obter volumes
        const responseVolumes = await api.get(`/volume/all`);
        if (responseVolumes.data && Array.isArray(responseVolumes.data)) {
          volumes.value = responseVolumes.data;
        } else {
          volumes.value = [];
        }

        // Obter sensores disponíveis
        const responseSensors = await api.get('/sensor/all');
        availableSensors.value = responseSensors.data;

        // Associar sensores aos volumes
        volumes.value.forEach(volume => {
          volume.sensores = volume.sensoresIds
              ? volume.sensoresIds.map(sensorId => availableSensors.value.find(sensor => sensor.id === sensorId)).filter(sensor => sensor)
              : [];
          volume.selectedSensorId = ""; // Inicializa a seleção de sensor
        });

      } catch (error) {
        console.error("Erro ao obter volumes ou sensores:", error.message);
      }
    };

    // Chama a função de obter volumes e sensores ao montar o componente
    onMounted(() => {
      fetchVolumesAndSensors();
    });

    // Função de navegação
    const goToProdutoPage = (produtoId, volumeId) => {
      router.push(`/produtos/${produtoId}/volume/${volumeId}`); // Usando o router diretamente
    };

    const goToSensorPage = (sensorId) => {
      router.push({ name: 'Sensores', params: { id: sensorId } }); // Usando o router diretamente
    };

    const editVolume = (volumeId) => {
      router.push(`/editarVolume/${volumeId}`); // Usando o router diretamente
    };

    // Função para adicionar sensor ao volume
    const addSensorToVolume = async (volumeId) => {
      const volume = volumes.value.find(v => v.id === volumeId);
      const selectedSensorId = volume.selectedSensorId;

      if (!selectedSensorId) {
        alert("Por favor, selecione um sensor.");
        return;
      }

      if (volume.sensores.some(sensor => sensor.id === selectedSensorId)) {
        alert("Este sensor já foi adicionado a este volume.");
        return;
      }

      try {
        loading.value = true;
        const selectedSensor = availableSensors.value.find(sensor => sensor.id === selectedSensorId);
        if (!selectedSensor) {
          alert("Sensor não encontrado.");
          loading.value = false;
          return;
        }

        const sensorData = {
          tipo: selectedSensor.tipo,
          ativo: true,
          volumeId: volumeId,
        };

        const response = await api.patch(`/sensor/${selectedSensorId}`, sensorData);

        if (response.data) {
          alert("Sensor adicionado com sucesso!");
          volume.sensores.push(response.data); // Adiciona o sensor ao volume
        }

        volume.selectedSensorId = ""; // Limpa a seleção do sensor após a adição
      } catch (error) {
        console.error("Erro ao adicionar o sensor:", error.message);
      } finally {
        loading.value = false;
      }
    };

    // Retorna os dados para o template
    return {
      authStore,
      volumes,
      availableSensors,
      loading,
      goToProdutoPage,
      goToSensorPage,
      editVolume,
      addSensorToVolume,
    };
  },
};

</script>
