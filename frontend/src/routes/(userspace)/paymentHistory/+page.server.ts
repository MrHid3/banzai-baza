import type { PageServerLoad, Actions } from './$types';
import { fail } from '@sveltejs/kit';

export const load: PageServerLoad = async ({ fetch }) => {
    const res = await fetch('/api/payment/recent');
    if (!res.ok) throw new Error('Nie udało się pobrać płatności');
    const raw: { member: any; payments: any[] }[] = await res.json();

    // Flatten into a single Payment[] with member data injected
    const payments = raw.flatMap(({ member, payments }) =>
        payments.map(p => ({
            ...p,
            payer: { name: member.name, surname: member.surname },
            location: member.location
        }))
    );

    return { payments };
};

export const actions: Actions = {
    deletePayment: async ({ request, fetch }) => {
        const data = await request.formData();
        const uuid = data.get('uuid');
        if (!uuid || typeof uuid !== 'string') return fail(400, { message: 'Brak uuid' });

        const res = await fetch(`/api/payments/${uuid}`, { method: 'DELETE' });
        if (!res.ok) return fail(res.status, { message: 'Nie udało się usunąć płatności' });

        return { success: true };
    }
};