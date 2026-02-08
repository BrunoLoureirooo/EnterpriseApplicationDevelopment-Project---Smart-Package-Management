<template>
  <div class="min-h-screen bg-gray-50 p-8">
    <div class="flex items-center mb-8">
      <button
          @click="$router.back()"
          class="px-4 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
      >
        Voltar
      </button>
      <h1 class="text-4xl font-bold text-center flex-grow text-gray-800">Criar Novo Produto</h1>
    </div>

    <!-- Formulário de Criação de Produto -->
    <div class="max-w-lg mx-auto bg-white p-8 rounded-lg shadow-lg">
      <form @submit.prevent="createProduct">
        <div class="mb-6">
          <label for="nome" class="block text-lg font-medium text-gray-700">Nome do Produto</label>
          <input
              v-model="produto.nome"
              id="nome"
              type="text"
              placeholder="Digite o nome do produto"
              class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
          />
        </div>

        <div class="mb-6">
          <label for="descricao" class="block text-lg font-medium text-gray-700">Descrição</label>
          <textarea
              v-model="produto.descricao"
              id="descricao"
              placeholder="Digite a descrição do produto"
              class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
              rows="4"
              required
          ></textarea>
        </div>

        <div class="mb-6">
          <label for="categoria" class="block text-lg font-medium text-gray-700">Categoria</label>
          <select
              v-model="produto.categoria"
              id="categoria"
              class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
          >
            <option value="">Selecione a categoria</option>
            <option value="ELETRODOMESTICOS">Eletrodomésticos</option>
            <option value="COMPUTADORES">Computadores</option>
            <option value="SMART_HOME">Smart Home</option>
            <option value="ARTES_MANUAIS">Artes Manuais</option>
            <option value="AUTOMOVEL">Automóvel</option>
            <option value="BEBE">Bebê</option>
            <option value="BELEZA_E_CUIDADO_PESSOAL">Beleza e Cuidados Pessoais</option>
            <option value="MODA_FEMININA">Moda Feminina</option>
            <option value="MODA_MASCULINA">Moda Masculina</option>
            <option value="MODA_INFANTIL">Moda Infantil</option>
            <option value="SAUDE">Saúde</option>
            <option value="CASA_E_COZINHA">Casa e Cozinha</option>
            <option value="CIENCIAS">Ciências</option>
            <option value="FILMES_E_TV">Filmes e TV</option>
            <option value="ANIMAIS">Animais</option>
            <option value="DESPORTO">Desporto</option>
            <option value="FERRAMENTAS">Ferramentas</option>
            <option value="BRINQUEDOS">Brinquedos</option>
            <option value="VIDEOJOGOS">Videojogos</option>
          </select>
        </div>

        <div class="mb-6">
          <label for="treshold" class="block text-lg font-medium text-gray-700">Limite (Treshold)</label>
          <input
              v-model="produto.treshold"
              id="treshold"
              type="number"
              step="0.01"
              placeholder="Digite o limite (treshold)"
              class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
          />
        </div>

        <div class="mb-6">
          <label for="tresholdTipo" class="block text-lg font-medium text-gray-700">Tipo de Limite</label>
          <select
              v-model="produto.tresholdTipo"
              id="tresholdTipo"
              class="px-4 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
          >
            <option value="">Selecione o tipo de limite</option>
            <option value="GPS">GPS</option>
            <option value="TEMPERATURA">Temperatura</option>
            <option value="ACELERACAO">Aceleração</option>
            <option value="PRESSAO">Pressão</option>
          </select>
        </div>

        <div class="text-center">
          <button
              type="submit"
              class="px-6 py-2 bg-green-500 text-white font-semibold rounded-lg hover:bg-green-600 transition duration-300 ease-in-out"
          >
            Criar Produto
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    // Variáveis reativas
    const produto = ref({
      nome: '',
      descricao: '',
      categoria: '',
      treshold: 0.0,
      tresholdTipo: '',
    });

    // Função para obter o token do Utilizador
    const getUserToken = async () => {
      try {
        await authStore.getToken();
        if (!authStore.isLoggedIn) {
          throw new Error("Utilizador não autenticado.");
        }
      } catch (error) {
        console.error(error.message);
        router.push('/login'); // Redirecionar para o login caso não esteja autenticado
      }
    };

    // Função para criar um novo produto
    const createProduct = async () => {
      try {
        if (authStore.user.role !== 'Gestor') {
          alert("Você não tem permissão para criar um produto.");
          return;
        }

        // Enviando os dados para a API
        await api.post('/produto', produto.value);
        alert("Produto criado com sucesso!");

        // Redirecionando para a lista de produtos
        router.push('/produtos/all');
      } catch (error) {
        console.error("Erro ao criar produto:", error.message);
        alert("Erro ao criar produto. Tente novamente.");
      }
    };

    // Chama a função para verificar o token e o login do Utilizador
    onMounted(() => {
      getUserToken();
    });

    return {
      produto,
      createProduct,
    };
  },
};
</script>
