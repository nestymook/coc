import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authApi } from '../api';

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null);
  const token = ref(localStorage.getItem('token') || null);
  const loading = ref(false);
  const error = ref(null);

  const isAuthenticated = computed(() => !!user.value);
  const getUserRole = computed(() => user.value?.role || 'RS');
  const getUserDepartment = computed(() => user.value?.department || '');

  const login = async (username, password) => {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await authApi.login(username, password);
      
      if (response.data.success) {
        token.value = response.data.token;
        user.value = response.data.userDetails;
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.userDetails));
        error.value = null;
        return { success: true };
      } else {
        error.value = response.data.message || '登录失败';
        return { success: false, message: error.value };
      }
    } catch (err) {
      error.value = err.response?.data?.message || '登录失败';
      return { success: false, message: error.value };
    } finally {
      loading.value = false;
    }
  };

  const logout = () => {
    user.value = null;
    token.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const loadUser = () => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      user.value = JSON.parse(storedUser);
    }
  };

  return {
    user,
    token,
    loading,
    error,
    isAuthenticated,
    getUserRole,
    getUserDepartment,
    login,
    logout,
    loadUser
  };
});