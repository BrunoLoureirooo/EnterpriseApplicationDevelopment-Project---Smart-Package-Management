<template>
  <div class="bg-black">
    <header class="bg-black grid grid-cols-2 items-center px-6 py-4">
      <!-- Logo Section -->
      <div class="text-3xl text-white font-bold justify-start flex">
        <router-link to="/">
          SW
        </router-link>
      </div>

      <!-- Anon -->
      <div v-if="!authStore.isLoggedIn && page == 'register'" class="text-white font-bold flex justify-end px-6">
        <router-link to="/login" class="text-white justify-end hover:text-gray-200">Login</router-link>
      </div>

      <div v-else-if="!authStore.isLoggedIn && page == 'login'" class="text-white font-bold flex justify-end px-6">
        <router-link to="/register" class="text-white justify-end hover:text-gray-200">Registar</router-link>
      </div>

      <div v-else-if="!authStore.isLoggedIn" class="text-white font-bold flex justify-end px-6">
        <router-link to="/login" class="text-white justify-end hover:text-gray-200">Login</router-link>
        <router-link to="/register" class="hover:text-gray-200 mx-2">Registar</router-link>
      </div>

       <!-- Funcionario Section -->
      <div v-if="authStore.isUserLoggedIn && authStore.user?.role === 'Funcionario'" class="text-white font-bold flex justify-end px-6">
        <router-link to="/sensor" class="hover:text-gray-200 mx-2">Sensores</router-link>
        <router-link to="/volumes/all" class="hover:text-gray-200 mx-2">Volumes</router-link>
        <router-link to="/perfil" class="hover:text-gray-200 mx-2">Perfil</router-link>
        <router-link to="/login" class="hover:text-gray-200 mx-2">Logout</router-link>
      </div>

      <!-- Gestor Section -->
      <div v-if="authStore.isUserLoggedIn && authStore.user?.role === 'Gestor'" class="text-white font-bold flex justify-end px-6">
        <router-link to="/sensor" class="hover:text-gray-200 mx-2">Sensores</router-link>
        <router-link to="/sensoresAuth" class="hover:text-gray-200 mx-2">SensoresContas</router-link>
        <router-link to="/gestores" class="hover:text-gray-200 mx-2">Gestores</router-link>
        <router-link to="/funcionarios" class="hover:text-gray-200 mx-2">Funcionarios</router-link>
        <router-link to="/clientes" class="hover:text-gray-200 mx-2">Clientes</router-link>
        <router-link to="/produtos/all" class="hover:text-gray-200 mx-2">Produtos</router-link>
        <router-link to="/encomendas" class="hover:text-gray-200 mx-2">Encomendas</router-link>
        <router-link to="/perfil" class="hover:text-gray-200 mx-2">Perfil</router-link>
        <router-link to="/login" class="hover:text-gray-200 mx-2">Logout</router-link>
      </div>

      <!-- Sensor Section -->
      <div v-if="authStore.isUserLoggedIn && authStore.user?.role === 'SensorAuth'" class="text-white font-bold flex justify-end px-6">
        <router-link to='/sensor' class="hover:text-gray-200 mx-2">Sensores</router-link>
        <router-link to="/perfil" class="hover:text-gray-200 mx-2">Perfil</router-link>
        <router-link to="/login" class="hover:text-gray-200 mx-2">Logout</router-link>
      </div>

      <!-- User Section -->
      <div v-if="authStore.isUserLoggedIn && authStore.user?.role === 'Cliente'" class="text-white font-bold flex justify-end px-6">
        <router-link to="/encomendar" class="hover:text-gray-200 mx-2">Encomendar</router-link>
        <router-link to="/encomendas" class="hover:text-gray-200 mx-2">Minhas Encomendas</router-link>
        <router-link to="/perfil" class="hover:text-gray-200 mx-2">Perfil</router-link>
        <router-link to="/login" class="hover:text-gray-200 mx-2">Logout</router-link>
      </div>
    </header>
  </div>
</template>

<script>
import { useAuthStore } from "@/stores/auth";

export default {
  data() {
    return {
      page: this.$route.path.split('/').pop(), // Obtém o último segmento do caminho
    };
  },
  

  name: "Header",
  setup() {
    const authStore = useAuthStore(); // Access the auth store

    // Certifique-se de que o token é obtido corretamente
    authStore.getToken();

    return {
      authStore,
    };
  },
  computed: {
    page() {
      // Automatically updates when the route changes
      return this.$route.path.split('/').pop(); 
    },
  },
};
</script>
