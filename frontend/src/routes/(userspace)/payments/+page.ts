import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load: PageLoad = async (event) => {

    const paymentsRequest = await event.fetch('/api/payment/recent')
    const multipliersRequest = await event.fetch('/api/multiplier')

    if(paymentsRequest.ok && multipliersRequest.ok) {
        const paymentJson = await paymentsRequest.json()
        const multipliersJson = await multipliersRequest.json()
        return {
            payments: paymentJson,
            multipliers: multipliersJson
        };
    }else{
        return { error: true }
    }
};
