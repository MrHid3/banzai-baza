import type { PageLoad } from './$types';

export const load: PageLoad = async (event) => {
	const paymentsRequest = await event.fetch('/api/payment', { method: 'GET' });

	if (paymentsRequest.ok) {
		const payments = await paymentsRequest.json();
		return { payments };
	} else {
		return { error: true, payments: [] };
	}
};
