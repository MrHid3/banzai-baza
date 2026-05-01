import {type Actions, fail, redirect} from "@sveltejs/kit";
import {serverFetch} from "$lib/api";


export const actions: Actions = {
    confirm: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const password  = data.get("password");
        // @ts-ignore
        if(!password?.match(/.{8,}/) ||
            // @ts-ignore
            !password?.match(/.*[A-Z].*/) ||
            // @ts-ignore
            !password?.match(/.*[a-z].*/) ||
            // @ts-ignore
            !password?.match(/.*[0-9].*/) ||
            // @ts-ignore
            !password?.match(/.*[!@#$%^&*()_+=\-\[\]{};'\\:"|<>?,.\/].*/)){
            return fail(400, {
                error: "Hasło musi zawierać małą i wielką literę, cyfrę, znak specjalny oraz conajmniej 8 znaków"
            })
        }

        const res = await serverFetch('/api/auth/reset-password/confirm', {
                method: "POST",
                body: JSON.stringify({
                    token: data.get("token"),
                    password: password
                })
            },
            cookies,
            locals);

        if(!res.ok){
            const body = await res.text();
            return fail(res.status, {
                error: body,
                values: Object.fromEntries(data)
            });
        }

        return {
            ok: true
        }
    }
}