import {type Actions, fail} from "@sveltejs/kit";
import {serverFetch} from "$lib/api";

export const actions : Actions = {
    request: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            '/api/auth/reset-password/request',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: data.get("email")
                }),
            },
            cookies,
            locals
        )

        if (!res.ok) {
            const body = await res.text();
            return fail(res.status, {
                error: body,
                values: Object.fromEntries(data)
            });
        }

        return {
            ok: true
        }
    },
}
