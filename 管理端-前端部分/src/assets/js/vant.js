import { Overlay, Button, Loading, Uploader,Dialog,Popup, Toast } from 'vant'

const vant = {
    install: function(Vue) {
        Vue.use(Overlay)
        Vue.use(Button)
        Vue.use(Loading)
        Vue.use(Uploader)
        Vue.use(Dialog)
        Vue.use(Popup)
        Vue.use(Toast)
    }
}

export default vant