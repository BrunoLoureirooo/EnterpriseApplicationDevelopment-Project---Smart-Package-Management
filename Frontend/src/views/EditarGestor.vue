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
      <h1 class="text-4xl font-bold text-center flex-grow">Editar Gestor</h1>
    </div>

    <!-- Formulário de Edição de Gestor -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Edite os dados do gestor</h2>

      <form @submit.prevent="editGestor">
        <div class="mb-4">
          <label class="text-lg font-medium">ID (Nome de Utilizador):</label>
          <input
              v-model="gestor.username"
              type="text"
              required
              disabled
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

        <div class="mt-6 flex justify-end">
          <button
              type="submit"
              class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
          >
            Atualizar Gestor
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/api.js";
import { useAuthStore } from "@/stores/auth.js";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const gestor = ref({
      username: "",
      nome: "",
      email: "",
    });

    // Verifica se o Utilizador é um gestor, se não, redireciona para login
    if (!authStore.isLoggedIn || authStore.user.role !== "Gestor") {
      router.push("/login");
    }

    // Função para carregar os dados do gestor
    const fetchGestor = async () => {
      const gestorId = router.currentRoute.value.params.id;  // Obtém o ID do gestor da URL
      try {
        const response = await api.get(`/gestor/${gestorId}`); // Chama a API para obter os dados do gestor
        if (response.data) {
          gestor.value = response.data;
        }
      } catch (error) {
        console.error("Erro ao obter dados do gestor:", error);
      }
    };

    // Função para editar o gestor
    const editGestor = async () => {
      try {
        const gestorId = router.currentRoute.value.params.id;
        const response = await api.patch(`/gestor/${gestorId}`, gestor.value); // Chama a API para editar o gestor
        if (response.status === 200) {
          alert("Gestor atualizado com sucesso!");
          router.push("/gestores"); // Redireciona para a lista de gestores
        } else {
          alert("Erro ao atualizar o gestor.");
        }
      } catch (error) {
        console.error("Erro ao atualizar o gestor:", error);
        alert("Erro ao tentar atualizar o gestor.");
      }
    };

    // Carregar os dados do gestor ao montar o componente
    onMounted(() => {
      fetchGestor();
    });

    // Retorna os dados para o template
    return {
      gestor,
      editGestor,
    };
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
