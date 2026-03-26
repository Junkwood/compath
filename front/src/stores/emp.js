import {defineStore} from 'pinia';
import axios from 'axios';


export const useEmpStore = defineStore('emp', {
    state: () => ({
        userList : [],
    }),
    getters: {

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
        async getEmpList(){
            await axios.get('/api/emp/list').then((response) => {
                this.userList=response.data;
            });
        }
    }
})