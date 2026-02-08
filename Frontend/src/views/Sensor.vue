<template>
    <div class="min-h-screen bg-gray-100 p-8">
      <div class="flex items-center mb-8">
        <!-- Back button aligned to the left -->
        
      <h1 class="text-4xl font-bold text-center flex-grow">
        Sensores
      </h1>
    </div>
  
      <!-- Lista de Sensores -->
      <div v-if="authStore.isUserLoggedIn && authStore.user?.role !== 'SensorAuth'" class="grid grid-cols-1 gap-8">
        <div
          v-for="(sensor) in sensores"
          :key="sensor.id"
          class="bg-white rounded-lg shadow-md p-6"
        >
          <!-- Sensor Information -->
          <div>
            <h2 class="text-2xl font-bold text-black mb-4">Código: {{ sensor.id }}</h2>
            <p class="text-lg font-medium">
              Estado: 
              <span 
                :class="sensor.ativo ? 'text-green-500' : 'text-red-500'"
                @click="toggleAtivo(sensor)"
                class="cursor-pointer"
              >
                {{ sensor.ativo ? 'Ativo' : 'Inativo' }}
              </span>
            </p>
            <p class="text-lg font-medium">Sensor: {{ sensor.tipo }}</p>
            <p class="text-lg font-medium">VolumeId: {{ sensor.volumeId }}</p>
          </div>
        </div>
      </div>

      <div v-if="authStore.isUserLoggedIn && authStore.user?.role === 'SensorAuth'" class="grid grid-cols-1 gap-8">
        <div
          v-for="(sensor) in sensores"
          :key="sensor.id"
          class="bg-white rounded-lg shadow-md p-6"
        >
        <!-- Sensor Information -->
          <div>
            <a :href="'/addMedicoes/' + sensor.id">
              <h2 class="text-2xl font-bold text-black mb-4">Código: {{ sensor.id }}</h2>
              <p class="text-lg font-medium">
                Estado:  {{ sensor.ativo ? 'Ativo' : 'Inativo' }}
              </p>
              <p class="text-lg font-medium">Sensor: {{ sensor.tipo }}</p>
              <p class="text-lg font-medium">VolumeId: {{ sensor.volumeId }}</p>
            </a>
          </div>
        </div>
      </div>
    </div>
  </template>
  
<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";
import { useRouter } from "vue-router";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    // Ensure the token is retrieved first
    authStore.getToken().then(() => {
      // Now check if the user is logged in
      if (!authStore.user) {
        router.push("/login");
      }
    });

    return {
      authStore,
      router,  // Ensure router is returned
    };
  },
  data() {
    return {
      sensores: [], // Lista de sensores
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

      console.log(authStore.user.role);
      let endPoint;
      if (authStore.user.role !== 'Client') {
        endPoint = "/sensor/all";
      } else {
        this.$router.push("/login");
      }

      // Fetch sensors data from the API
      const response = await api.get(endPoint);
      this.sensores = response.data;
      console.log(this.sensores);
    } catch (error) {
      console.error("Error fetching sensors:", error);
    }
  },
  methods: {
   
    async toggleAtivo(sensor) {
      // Inverter o estado do sensor
      const updatedAtivo = !sensor.ativo; 

      // Update ao sensor
      const updatedSensor = {
        id: sensor.id,
        ativo: updatedAtivo,
        tipo: sensor.tipo,
        volumeId: sensor.volumeId,
      };

      // Enviar o pedido PATCH para atualizar o sensor
      try {
        const response = await api.patch(`/sensor/${sensor.id}`, updatedSensor);

        if (response.status === 200) {
          sensor.ativo = updatedAtivo; 
          alert("Status atualizado com sucesso!");
        } else {
          alert("Erro ao atualizar status.");
        }
      } catch (error) {
        console.error("Erro ao atualizar status:", error);
        alert("Ocorreu um erro ao tentar atualizar o status.");
      }
    },
  },
};
</script>

  