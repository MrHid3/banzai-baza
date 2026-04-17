import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load: PageLoad = async (event) => {

    const res1 = await event.fetch(`/api/member`, {method: "GET"})
    const paymentsRequest = await event.fetch('/api/payment/recent')


    if(res1.ok && paymentsRequest.ok){
        const res1json = await res1.json();
        const paymentJson = await paymentsRequest.json()
        return {
            members: res1json,
            payments: paymentJson
        };
    }else{
        return { error: true }
    }
};
