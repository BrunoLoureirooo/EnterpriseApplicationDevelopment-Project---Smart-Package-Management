import api from '/src/api/api.js';
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
    const token = ref('');
    const user = ref(null);
    const isLoggedIn = ref(false);

    const isUserLoggedIn = computed(() => isLoggedIn.value);

    const checkValue = (value) => {
        /*
        should get a json like this:
            {
                "username": "string",
                "password": "string"
            }
        */
        if (typeof value !== 'object' || value === null) {
            return false;
        }

        const { username, password } = value;

        return typeof username === 'string' && typeof password === 'string';
    };

    //save the token in storage
    const saveToken = () =>{
        localStorage.setItem('token', token.value);
    }

    //get the token from storage
    const getToken = async () => {
        token.value = localStorage.getItem('token');
        if (token.value) {
            api.defaults.headers.common = {
                Authorization: `Bearer ${token.value}`,
                'Content-Type': 'application/json',
                Accept: 'application/json',
            }
            isLoggedIn.value = true;
            await getUser();
        }
    }

    const login = async (credentials) => {
        if (!checkValue(credentials)) {
            throw new Error('Invalid credentials');
        }
        const response = await api.post('auth/login', credentials, {
            responseType: 'text',
        });
        const tokenValue = response.data;
        if(!tokenValue){
            throw new Error('Invalid credentials');
        }
        token.value = tokenValue;
        saveToken();
        api.defaults.headers.common = {
            Authorization: `Bearer ${token.value}`,
            'Content-Type': 'application/json',
            Accept: 'application/json',
        }
        isLoggedIn.value = true;
        await getUser();
    };

    const getUser = async () => {
        const response = await api.get('auth/user', {
            headers: {
                Authorization: `Bearer ${token.value}`,
            },
        });
        user.value = response.data;
    }

    const logout = () => {
        token.value = '';
        user.value = null;
        isLoggedIn.value = false;
        localStorage.removeItem('token');
    }

    return {
        token,
        user,
        isLoggedIn,
        login,
        logout,
        getToken,
        isUserLoggedIn,
    };
});
