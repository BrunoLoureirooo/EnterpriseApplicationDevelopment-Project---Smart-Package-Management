<template>
  <div class="min-h-screen bg-gray-100 p-8">
    <!-- Cabeçalho -->
    <div class="flex items-center mb-8">
     
      <h1 class="text-4xl font-bold text-center flex-grow">Perfil de Utilizador</h1>
    </div>

    <!-- Dados do Perfil -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold text-black mb-4">Informações do Perfil</h2>

      <div class="mb-4">
        <label class="text-lg font-medium">Nome de Utilizador:</label>
        <p class="text-lg">{{ user.username }}</p>
      </div>

      <div class="mb-4">
        <label class="text-lg font-medium">Nome Completo:</label>
        <p class="text-lg">{{ user.nome }}</p>
      </div>

      <div class="mb-4">
        <label class="text-lg font-medium">Email:</label>
        <p class="text-lg">{{ user.email }}</p>
      </div>

      <div class="mb-4">
        <label class="text-lg font-medium">Função:</label>
        <p class="text-lg">{{ user.role }}</p>
      </div>

      <!-- Botões de ação -->
      <div class="mt-6 flex space-x-4">
        <button
            @click="editProfile"
            class="px-6 py-2 bg-yellow-500 text-white font-semibold rounded hover:bg-yellow-600"
        >
          Editar
        </button>
        <button
            @click="deleteProfile"
            class="px-6 py-2 bg-red-500 text-white font-semibold rounded hover:bg-red-600"
        >
          Apagar
        </button>
        <button
          @click="changePassword"
          class="px-6 py-2 bg-blue-500 text-white font-semibold rounded hover:bg-red-600"
          >
          Mudar Password
        </button>
      </div>
      
    </div>

    <!-- Send Email -->
    <div v-if="authStore.isUserLoggedIn && authStore.user?.role !== 'SensorAuth'" class="bg-white rounded-lg shadow-md p-6 mt-8">
      <h2 class="text-2xl font-bold text-black mb-4">Mandar Email</h2>
      <form id="emailForm" class="space-y-4">
        <!-- Username Input -->
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
          <input
            type="text"
            id="username"
            name="username"
            class="mt-1 p-2 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter username"
            required
          />
        </div>

        <!-- Subject Input -->
        <div>
          <label for="subject" class="block text-sm font-medium text-gray-700">Subject</label>
          <input
            type="text"
            id="subject"
            name="subject"
            class="mt-1 p-2 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter subject"
            required
          />
        </div>

        <!-- Body Input -->
        <div>
          <label for="body" class="block text-sm font-medium text-gray-700">Body</label>
          <textarea
            id="body"
            name="body"
            class="mt-1 p-2 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter email body"
            rows="4"
            required
          ></textarea>
        </div>

        <!-- Send Button -->
        <button
          type="button"
          @click="sendEmail"
          class="bg-blue-500 text-white font-bold py-2 px-4 rounded hover:bg-blue-600"
        >
          Send Email
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from "@/stores/auth.js";
import api from "@/api/api.js";
import { useRouter } from "vue-router";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter(); 
    
    if (!authStore.user) {
      router.push("/login");
    }

    return {
      authStore,
    };
  },
  data() {
    return {
      user: {}, // Dados do Utilizador
    };
  },
  created() {
    const authStore = useAuthStore();
    authStore.getToken();
    if (authStore.user) {
      this.user = authStore.user;
    } else {
      console.error("Utilizador não encontrado!");
    }
  },
  methods: {
// Method to send email
  async sendEmail() {
      const username = document.getElementById("username").value;
      const subject = document.getElementById("subject").value;
      const body = document.getElementById("body").value;

      // Validate inputs
      if (!username || !subject || !body) {
        alert("Por favor, preencha todos os campos.");
        return;
      }

      try {
        // Make API request to send email
        const response = await api.post(`/email/${username}/send`, {
          subject,
          body,
        });

        if (response.status === 200) {
          alert("Email enviado com sucesso!");
        } else {
          alert("Erro ao enviar email.");
        }
      } catch (error) {
        console.error("Erro ao enviar email:", error);
        alert("Ocorreu um erro ao tentar enviar o email.");
      }
    },
    // Método para mudar a password
    changePassword() {
      // Redirecionar para a página de mudança de password (ajuste conforme a sua rota)
      this.$router.push("/mudar-password");
    },
    // Método para editar perfil
    editProfile() {
      // Redirecionar para a página de edição (ajuste conforme a sua rota)
      this.$router.push("/editar-perfil");
    },
    // Método para apagar o perfil
    async deleteProfile() {
      const confirmation = confirm("Tem certeza que deseja apagar o seu perfil?");
      if (confirmation) {
        try {
          // Lógica para apagar o perfil
          // Pode ser uma chamada para uma API para excluir o Utilizador ou deslogá-lo
          const authStore = useAuthStore();
          await authStore.logout();  // Exemplo de logout após a exclusão
          this.$router.push("/login");  // Redireciona para a tela de login após a exclusão
          const response = await api.delete('cliente/'+this.user.username); // Exemplo de chamada para deletar o Utilizador
          if(response.status === 200){
            alert("Perfil apagado com sucesso!");
          } else {
            alert("Erro ao apagar o perfil.");
          }
        } catch (error) {
          console.error("Erro ao apagar o perfil:", error);
          alert("Erro ao tentar apagar o perfil.");
        }
      }
    },
  },
};
</script>
