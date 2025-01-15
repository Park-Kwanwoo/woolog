import {defineStore} from "pinia";
import {computed, ref} from "vue";

export const useTokenStore = defineStore('token', () => {

    const token = ref<string | null>(null)
    const isAuthenticated = computed((): string => !!token.value)

    function setToken(value: string) {
      token.value = value;
      localStorage.setItem('token', token.value)
    }

    function deleteToken() {
      token.value = null;
    }

    return {
      token,
      isAuthenticated,
      setToken,
      deleteToken
    };
  },
  {
    persist:true
  }
)
