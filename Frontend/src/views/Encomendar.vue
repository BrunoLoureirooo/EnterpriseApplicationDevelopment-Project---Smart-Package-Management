<template>
  <div class="min-h-screen bg-gray-100 p-8 flex">
    <!-- Lista de Produtos -->
    <div class="flex-1 bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Produtos Disponíveis</h2>

      <table class="table-auto w-full mb-6">
        <thead>
        <tr>
          <th class="border px-4 py-2">Nome</th>
          <th class="border px-4 py-2">Descrição</th>
          <th class="border px-4 py-2">Categoria</th>
          <th class="border px-4 py-2">Ação</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="produto in produtos" :key="produto.id">
          <td class="border px-4 py-2">{{ produto.nome }}</td>
          <td class="border px-4 py-2">{{ produto.descricao }}</td>
          <td class="border px-4 py-2">{{ produto.categoria }}</td>
          <td class="border px-4 py-2">
            <input
                type="number"
                v-model.number="quantidades[produto.id]"
                min="1"
                placeholder="Quantidade"
                class="w-20 p-2 border border-gray-300 rounded"
            />
            <button
                @click="addToCart(produto)"
                class="ml-2 px-4 py-2 bg-green-500 text-white font-semibold rounded hover:bg-green-600"
            >
              Adicionar
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Barra Lateral do Carrinho -->
    <div class="w-1/4 bg-white rounded-lg shadow-md p-6 ml-4">
      <h2 class="text-2xl font-bold text-black mb-4">Carrinho</h2>

      <ul class="list-disc list-inside mb-4">
        <li v-for="item in carrinho" :key="item.produto.id">
          {{ item.produto.nome }} - Quantidade: {{ item.quantidade }}
          <button
              @click="removeFromCart(item.produto.id)"
              class="ml-4 px-2 py-1 bg-red-500 text-white font-semibold rounded hover:bg-red-600"
          >
            Remover
          </button>
        </li>
      </ul>

      <div class="mb-4">
        <label for="morada" class="block text-gray-700">Morada:</label>
        <input
            type="text"
            v-model="morada"
            id="morada"
            placeholder="Insira a sua morada"
            class="w-full p-2 border border-gray-300 rounded"
        />
      </div>

      <button
          @click="enviarEncomenda"
          :disabled="isSending || carrinho.length === 0 || !morada"
          class="w-full px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600 disabled:opacity-50"
      >
        Enviar Encomenda
      </button>
    </div>
  </div>
</template>

<script>
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth";
import router from "@/router/index.js";

export default {
  data() {
    return {
      produtos: [],
      carrinho: [],
      quantidades: {},
      morada: "",
      isSending: false,
    };
  },
  async created() {
    try {
      const response = await api.get("/produto/all");
      this.produtos = response.data;
    } catch (error) {
      console.error("Erro ao buscar produtos:", error);
    }
  },
  methods: {
    addToCart(produto) {
      const quantidade = this.quantidades[produto.id];
      if (quantidade > 0) {
        const itemExistente = this.carrinho.find((item) => item.produto.id === produto.id);
        if (itemExistente) {
          itemExistente.quantidade += quantidade;
        } else {
          this.carrinho.push({produto, quantidade});
        }
        this.quantidades[produto.id] = 0;
      }
    },
    removeFromCart(produtoId) {
      this.carrinho = this.carrinho.filter((item) => item.produto.id !== produtoId);
    },
    async enviarEncomenda() {
      const authStore = useAuthStore();
      await authStore.getToken();
      if (!authStore.isUserLoggedIn) {
        alert("Por favor faça login para enviar a encomenda.");
        await router.push("/login");
        return;
      }

      const clienteUsername = authStore.user.username;

      const produtosIds = this.carrinho.map((item) => item.produto.id);
      const produtosQuants = this.carrinho.map((item) => item.quantidade);

      const volumeDTO = {
        estado: "ATIVO",
        sensoresIds: [],
        produtosIds,
        produtosQuants,
      };

      try {
        this.isSending = true;
        const volumeResponse = await api.post("/volume", volumeDTO);

        if (volumeResponse.status === 200 || volumeResponse.status === 201) {
          const volumeIds = volumeResponse.data.map(volume => volume.id); // múltiplos volumes

          const encomendaDTO = {
            codigo: Date.now(),
            estado: "PENDENTE",
            volumeIds,
            morada: this.morada,
            clienteUsername,
          };

          const encomendaResponse = await api.post("/encomenda", encomendaDTO);

          if (encomendaResponse.status === 200 || encomendaResponse.status === 201) {
            alert("Volume(s) e encomenda enviados com sucesso!");
            this.carrinho = [];
            this.morada = "";
          } else {
            alert("Erro ao enviar a encomenda.");
          }
        } else {
          alert("Erro ao enviar o volume.");
        }
      } catch (error) {
        console.error("Erro ao enviar volume(s) e encomenda:", error);
        alert("Erro ao tentar enviar o volume(s) e encomenda.");
      } finally {
        this.isSending = false;
      }
    },
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
