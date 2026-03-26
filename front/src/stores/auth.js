import {defineStore} from 'pinia';


export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem("token")||null,
        user: JSON.parse(localStorage.getItem('user')) || null, // 새로고침 시 로컬스토리지에서 복구
    }),
    getters: {
        isLoggedIn: state => !!state.user,
        isAdmin: state => state.user?.userType === 'ADMIN',
        userName: state => state.user?.name || '',
    },
    actions: {
        login(userData,token = null){
            this.user=userData;;
            this.token=token;
            localStorage.setItem('user',JSON.stringify(userData));
            if(token){
                localStorage.setItem('token',token);
            }
        },
        logout(){
            this.user=null;
            this.token=null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');

        }
    }
})