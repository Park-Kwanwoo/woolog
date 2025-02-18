import {defineStore} from "pinia";
import {computed, ref} from "vue";

export const useTokenStore = defineStore('token', () => {

    const token = ref<String | null>()
    const isAuthenticated = computed((): boolean => !!token.value)

    function setToken(value: string) {
      token.value = value
      localStorage.setItem('token', token.value)
    }

    function deleteToken() {
      token.value = null;
      localStorage.removeItem('token')
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
