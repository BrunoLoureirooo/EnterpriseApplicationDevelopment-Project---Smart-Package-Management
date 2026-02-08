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
      <h1 class="text-4xl font-bold text-center flex-grow">Alterar Senha</h1>
    </div>

    <!-- Formulário de Mudança de Senha -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Alterar sua senha</h2>

      <form @submit.prevent="changePassword">
        <!-- Senha Antiga -->
        <div class="mb-4">
          <label class="text-lg font-medium">Senha Antiga:</label>
          <input
              v-model="passwordData.oldPassword"
              type="password"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <!-- Nova Senha -->
        <div class="mb-4">
          <label class="text-lg font-medium">Nova Senha:</label>
          <input
              v-model="passwordData.newPassword"
              type="password"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <!-- Confirmar Nova Senha -->
        <div class="mb-4">
          <label class="text-lg font-medium">Confirmar Nova Senha:</label>
          <input
              v-model="passwordData.confirmNewPassword"
              type="password"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <!-- Mensagem de Erro -->
        <div v-if="errorMessage" class="text-red-500 text-sm mb-4">
          {{ errorMessage }}
        </div>

        <div class="mt-6 flex justify-end">
          <button
              type="submit"
              class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-blue-600"
          >
            Atualizar Senha
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/api.js";

export default {
  setup() {
    const router = useRouter();
    const passwordData = ref({
      oldPassword: "",
      newPassword: "",
      confirmNewPassword: "",
    });
    const errorMessage = ref("");

    // Função para mudar a senha
    const changePassword = async () => {
      // Verificar se as senhas novas correspondem
      if (passwordData.value.newPassword !== passwordData.value.confirmNewPassword) {
        errorMessage.value = "As senhas novas não coincidem. Por favor, tente novamente.";
        return;
      }

      try {
        // Criar o DTO para a solicitação
        const changePasswordDTO = {
          oldPassword: passwordData.value.oldPassword,
          newPassword: passwordData.value.newPassword,
          confirmNewPassword: passwordData.value.confirmNewPassword,
        };

        // Enviar a solicitação para mudar a senha
        const response = await api.post("/auth/set-password", changePasswordDTO);

        if (response.status === 200) {
          alert("Senha alterada com sucesso!");
          router.push("/profile"); // Redireciona para a página do perfil do usuário
        } else {
          alert("Erro ao alterar a senha.");
        }
      } catch (error) {
        console.error("Erro ao mudar a senha:", error);
        alert("Erro ao tentar alterar a senha.");
      }
    };

    return {
      passwordData,
      errorMessage,
      changePassword,
    };
  },
};
</script>

<style scoped>
/* Estilos adicionais podem ser adicionados aqui, se necessário */
</style>
