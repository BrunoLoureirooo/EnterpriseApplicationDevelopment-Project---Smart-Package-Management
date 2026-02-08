<template>
    <div class="min-h-screen bg-gray-100 p-8">
      <div class="flex items-center mb-8">
        <button
          @click="$router.back()"
          class="px-4 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
        >
          Voltar
        </button>
        <!-- Back button aligned to the left -->
        <h1 class="text-4xl font-bold text-center flex-grow">
          Adicionar Medição ao Sensor Código {{ sensor_id }} 
        </h1>
      </div>
      <div class="flex items-center mb-8">
        <!-- Back button aligned to the left -->
        <h1 class="text-4xl font-bold text-center flex-grow">
          Sensor de {{ sensores.tipo }} 
        </h1>
      </div>
  
      <!-- Input for sending text -->
      <div class="mb-8">
        <label for="userInput" class="block text-lg font-medium mb-2">Medição:</label>
        <div class="flex">
          <input
            id="userInput"
            type="text"
            v-model="userInput"
            class="flex-grow p-2 border border-gray-300 rounded-l-lg"
            placeholder="Medicoes"
          />
          
          <button
            @click="sendInput"
            class="bg-blue-500 text-white px-4 py-2 rounded-r-lg hover:bg-blue-600"
          >
            Enviar
          </button>
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
        sensores: [],
        userInput: "", // For storing user input
        sensor_id: this.$route.params.id,    
      };
    },
    methods: {
      sendInput() {
        if (this.userInput.trim() === "") {
          alert("Por favor, escreva algo antes de enviar.");
          return;
        }
        const medicoes = {
          data: Date.now(),
          valor: this.userInput,
          sensor_id: this.sensor_id,
        };
        api.post(`/sensor/${medicoes.sensor_id}/medicoes`, medicoes);

        console.log("Medição enviada com sucesso!");
        console.log("Medição:", medicoes);
      },
    },
    async created() {
      try {
        const authStore = useAuthStore();
        
        await authStore.getToken();
        
        if (!authStore.isLoggedIn) {
          throw new Error("Utilizador não autenticado.");
        }
        const response = await api.get(`/sensor/${this.sensor_id}`);
        this.sensores = response.data;
      } catch (error) {
        console.error("Error fetching sensores:", error);
      }
    },
  };
  </script>
  