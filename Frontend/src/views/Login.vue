<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100">
    <!-- Login Card -->
    <div class="w-full max-w-md p-8 bg-white rounded-lg shadow-md">
      <h2 class="text-3xl font-bold text-center mb-6">Login</h2>

      <!-- Login Form -->
      <form @submit.prevent="handleLogin">
        <!-- Username Field -->
        <div class="mb-4">
          <label for="username" class="block text-sm font-medium text-black">Username</label>
          <input
              type="text"
              id="username"
              v-model="username"
              placeholder="Enter your username"
              class="w-full mt-1 p-2 border rounded-md focus:ring-red-500 focus:border-red-500"
              required
          />
        </div>

        <!-- Password Field -->
        <div class="mb-6">
          <label for="password" class="block text-sm font-medium text-black">Password</label>
          <input
              type="password"
              id="password"
              v-model="password"
              placeholder="Enter your password"
              class="w-full mt-1 p-2 border rounded-md focus:ring-red-500 focus:border-red-500"
              required
          />
        </div>

        <!-- Submit Button -->
        <button
            type="submit"
            class="w-full bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-400 focus:ring-opacity-50"
        >
          Login
        </button>
      </form>

      <!-- Error Message -->
      <p v-if="errorMessage" class="mt-4 text-sm text-red-500 text-center">
        {{ errorMessage }}
      </p>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth';
import { onMounted } from 'vue';
import router from "@/router/index.js"; // Import the store

export default {
  data() {
    return {
      username: '', // Bound to the username input
      password: '', // Bound to the password input
      errorMessage: '', // Error message for invalid login
    };
  },
  setup() {
    const authStore = useAuthStore();

    // Call the logout function on component mounted
    onMounted(() => {
      authStore.logout();
    });

    return {
      authStore,
    };
  },
  methods: {
    async handleLogin() {
      const authStore = useAuthStore(); // Access the auth store
      try {
        await authStore.login({username: this.username, password: this.password});
        this.errorMessage = '';
        // Redirect or handle successful login
        await router.push('/perfil');
      } catch (error) {
        console.error('Error during login:', error);
        this.errorMessage = 'Invalid username or password.';
      }
    },
  },
};
</script>

<style>
/* Optional: Add custom styles here */
</style>
