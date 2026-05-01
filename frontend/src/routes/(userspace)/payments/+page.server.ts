import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";
import {type Actions, fail} from "@sveltejs/kit";
import {serverFetch} from "$lib/api";

export const actions : Actions = {

    addPayment: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch('/api/payment',
            {
                method: 'POST',
                body: JSON.stringify({
                    paymentType: data.get('paymentType'),
                    amount: data.get('amount'),
                    month: data.get('month'),
                    year: data.get('year'),
                    payerUuid: data.get('payerUuid'),
                    paymentMethod: data.get('paymentMethod')
                })
            },
            cookies,
            locals
        );

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Nie udało się zapisać płatności',
                values: Object.fromEntries(data)
            });
        }
    },

    deletePayment: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch('/api/payment',
            {
                method: 'DELETE',
                body: JSON.stringify({
                    uuid: data.get('paymentUuid'),
                })
            },
            cookies,
            locals
        );

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Nie udało się usunąć płatności',
                values: Object.fromEntries(data)
            });
        }
    }
}