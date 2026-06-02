import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load: PageLoad = async (event) => {

    const paymentsRequest = await event.fetch('/api/payment/recent')
    const multipliersRequest = await event.fetch('/api/multiplier')
    const categories = await event.fetch("/api/memberCategory")

    if(paymentsRequest.ok && multipliersRequest.ok && categories.ok) {
        const paymentJson = await paymentsRequest.json()
        const multipliersJson = await multipliersRequest.json()
        return {
            payments: paymentJson,
            multipliers: multipliersJson,
            categories: await categories.json(),
        };
    }else{
        return { error: true }
    }
};
