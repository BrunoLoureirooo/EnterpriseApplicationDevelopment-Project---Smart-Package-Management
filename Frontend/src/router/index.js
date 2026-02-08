import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/Home.vue';
import Layout from '@/layout.vue';
import api from "@/api/api.js"; // Your main layout
import { useAuthStore } from '@/stores/auth'; // Importando a store de autenticação

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Login.vue'),
    meta: { layout: Layout }, // Default layout
  },

  // Add your routes here
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/encomendas',
    name: 'Encomenda',
    component: () => import('@/views/Encomendas.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/volumesEncomendas/:id?',
    name: 'VolumesEncomenda',
    component: () => import('@/views/volumesEncomendas.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/produtos/:id?/volume/:idVolume?',
    name: 'Produtos',
    component: () => import('@/views/Produtos.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/sensores/:id?',
    name: 'Sensores',
    component: () => import('@/views/Sensores.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/sensor',
    name: 'Sensor',
    component: () => import('@/views/Sensor.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/AddMedicoes/:id?',
    name: 'AddMedicoes',
    component: () => import('@/views/addMedicoes.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/perfil',
    name: 'Perfil',
    component: () => import('@/views/Perfil.vue'),
    meta: { layout: Layout },
  },

  {
    path: '/editar-perfil',
    name: 'EditarPerfil',
    component: () => import('@/views/EditarPerfil.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/register',
    name:'Register',
    component: () => import('@/views/Register.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/Encomendar',
    name:'Encomendar',
    component: () => import('@/views/Encomendar.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/editarEncomenda/:codigo',
    name: 'EditarEncomenda',
    component: () => import('@/views/EditarEncomendas.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/editarVolume/:id',
    name: 'EditarVolume',
    component: () => import('@/views/EditarVolume.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/produtos/all',
    name: 'ProdutosAll',
    component: () => import('@/views/ProdutosAll.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/produtos/criar',
    name: 'CriarProduto',
    component: () => import('@/views/CriarProduto.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/produtos/editar/:id',
    name:'EditarProduto',
    component: () => import('@/views/EditarProduto.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/clientes',
    name:'Clientes',
    component: () => import('@/views/Clientes.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/clientes/editar/:id',
    name: 'EditarCliente',
    component: () => import('@/views/EditarCliente.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/funcionarios',
    name: 'Funcionarios',
    component: () => import('@/views/Funcionarios.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/funcionarios/criar',
    name: 'CriarFuncionario',
    component: () => import('@/views/CriarFuncionario.vue'),
    meta: { layout: Layout },
  },
  {
    path: '/funcionarios/editar/:id',
    name: 'EditarFuncionario',
    component: () => import('@/views/EditarFuncionario.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/sensoresAuth',
    name:'SensoresAuth',
    component: () => import('@/views/SensoresAuth.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/sensoresAuth/criar',
    name:'CriarSensorAuth',
    component: () => import('@/views/CriarSensorAuth.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/sensoresAuth/editar/:id',
    name:'EditarSensorAuth',
    component: () => import('@/views/EditarSensorAuth.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/gestores',
    name:'Gestores',
    component: () => import('@/views/Gestores.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/gestores/criar',
    name:'CriarGestor',
    component: () => import('@/views/CriarGestor.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/gestores/editar/:id',
    name:'EditarGestor',
    component: () => import('@/views/EditarGestor.vue'),
    meta: { layout: Layout },
  },


  {
    path:'/mudar-password',
    name:'MudarPassword',
    component: () => import('@/views/MudarPassword.vue'),
    meta: { layout: Layout },
  },
  {
    path:'/volumes/all',
    name:'VolumesAll',
    component: () => import('@/views/VolumesAll.vue'),
    meta: { layout: Layout },
  }



];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Verificar se o usuário deve ser redirecionado antes de acessar a rota de Sensores
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  console.log("Interceptando rota:", to.name);

  if (to.name === 'Sensor' && authStore.isLoggedIn && authStore.user.role === 'SensorAuth') {
    try {
      const response = await api.get(`/sensorAuth/${authStore.user.username}`);
      console.log("Resposta da API:", response);

      if (response.status === 200) {
        const sensorId = response.data.sensor_id;
        next({ name: 'AddMedicoes', params: { id: sensorId } });
      } else {
        next();
      }
    } catch (error) {
      console.error("Erro ao buscar dados do sensor:", error);
      next();
    }
  } else {
    next();
  }
});


export default router;
