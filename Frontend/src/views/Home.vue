<template>
  <div class="flex flex-col items-center justify-center min-h-screen text-4xl">
    <!-- Top Header Section -->
    <div class="mb-40">
      <h1>Welcome to the Sigma Warehouse</h1>
    </div>

    <!-- Product Section -->
    <div class="grid grid-cols-3 gap-4 w-full px-8">
      <div 
        v-for="(product, index) in products" 
        :key="index" 
        class="p-4 border rounded-lg shadow-md text-center"
      >
        <h2 class="text-3xl font-bold">{{ product.nome }}</h2>
        <p class="text-lg text-gray-500">{{ product.categoria }}</p>
        <p class="text-lg text-black">   {{ product.descricao }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api.js';

export default {
  data() {
    return {
      products: [], // Initially empty, populated via API call
    };
  },
  async created() {
    try {
      const response = await api.get('/produto/all'); // Fetch products
      this.products = response.data; // Update the `products` array
    } catch (error) {
      console.error('Error fetching produtos:', error);
    }
  },
};
</script>

