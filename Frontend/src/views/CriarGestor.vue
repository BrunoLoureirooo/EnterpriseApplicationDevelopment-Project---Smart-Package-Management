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
      <h1 class="text-4xl font-bold text-center flex-grow">Criar Novo Gestor</h1>
    </div>

    <!-- Formulário de Criação de Gestor -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Preencha os dados do novo gestor</h2>

      <form @submit.prevent="createGestor">
        <div class="mb-4">
          <label class="text-lg font-medium">ID (Nome de Utilizador):</label>
          <input
              v-model="gestor.username"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Nome:</label>
          <input
              v-model="gestor.nome"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mb-4">
          <label class="text-lg font-medium">Email:</label>
          <input
              v-model="gestor.email"
              type="email"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <!-- Adicionando o campo de senha -->
        <div class="mb-4">
          <label class="text-lg font-medium">Senha:</label>
          <input
              v-model="gestor.password"
              type="password"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div class="mt-6 flex justify-end">
          <button
              type="submit"
              class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
          >
            Criar Gestor
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";
import {ref} from "vue";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const gestor = ref({
      username: "",
      nome: "",
      email: "",
      password: "",
    });

    // Verifica se o Utilizador é um gestor, se não, redireciona para login
    if (!authStore.isLoggedIn || authStore.user.role !== "Gestor") {
      router.push("/login");
    }

    // Função para criar o gestor
    const createGestor = async () => {
      try {
        const response = await api.post("/gestor", gestor.value); // Chama a API para criar o gestor
        if (response.status === 201) {
          alert("Gestor criado com sucesso!");
          router.push("/gestores");  // Redireciona para a lista de gestores
        } else {
          alert("Erro ao criar o gestor.");
        }
      } catch (error) {
        console.error("Erro ao criar o gestor:", error);
        alert("Erro ao tentar criar o gestor.");
      }
    };

    // Retorna os dados para o template
    return {
      gestor,
      createGestor,
    };
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
