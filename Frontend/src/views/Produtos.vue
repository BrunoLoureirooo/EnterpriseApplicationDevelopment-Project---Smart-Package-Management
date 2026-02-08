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
        Produto #{{ prodCod }} do Volume #{{ volCod }}
      </h1>
    </div>

    <!-- Lista de Produtos -->
    <div class="grid grid-cols-1 gap-8">
      <div
          v-for="(product, index) in products"
          :key="index"
          class="bg-white rounded-lg shadow-md p-6"
      >
        <h2 class="text-2xl font-bold text-black mb-4">
          Nome: {{ product.nome }}
        </h2>
        <p class="text-lg font-medium">Descrição: {{ product.descricao }}</p>
        <p class="text-lg font-medium">Categoria: {{ product.categoria }}</p>
        <p class="text-lg font-medium">Quantidade: {{ product.quantidade }}</p>
        <p class="text-lg font-medium">Estado: {{ product.estado }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  data() {
    return {
      products: [], // Lista de produtos
      prodCod: null, // Código do produto da URL
      volCod: null, // Código do volume da URL
    };
  },
  async created() {
    try {
      const authStore = useAuthStore();
      // Chama o getToken de forma assíncrona
      await authStore.getToken();

      // Verificar se está logado
      if (!authStore.isLoggedIn) {
        throw new Error("Utilizador não autenticado.");
      }

      // Obtendo o código do produto e o código do volume a partir dos parâmetros da rota
      this.prodCod = this.$route.params.id;
      this.volCod = this.$route.params.idVolume;

      // Buscar detalhes do produto e do volume usando os códigos na URL
      const productResponse = await api.get(`/volume/${this.volCod}/produto/${this.prodCod}`);
      const productDetails = await api.get(`/produto/${this.prodCod}`);

      // Se a resposta for válida, armazenar os dados combinados
      if (productResponse.data && productDetails.data) {
        const combinedProduct = {
          ...productDetails.data, // Dados do produto
          quantidade: productResponse.data.quantidade, // Quantidade do produto no volume
          estado: productResponse.data.estado, // Estado do produto no volume
        };

        this.products = [combinedProduct];  // Armazena os dados combinados
      } else {
        this.products = [];  // Caso não encontre o produto ou os detalhes, limpa a lista
      }
    } catch (error) {
      console.error("Error fetching product:", error.message);
      console.error("Full error details:", error.toJSON ? error.toJSON() : error);
    }
  },
};
</script>
