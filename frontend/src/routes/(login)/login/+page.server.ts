import {VITE_BACKEND_URL, VITE_DEV} from '$env/static/private'
import {fail, redirect} from "@sveltejs/kit";

export const actions = {
    login: async ({request, cookies}) => {

        const data = await request.formData();
        const email = data.get("email");
        const password = data.get("password");
        const rememberMe = data.get("remember");

        const res: any = await fetch(`${VITE_BACKEND_URL}/api/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const setCookie = res.headers.get("set-cookie");
        if (setCookie) {
            cookies.set("refreshToken", setCookie.split("=")[1], {
                httpOnly: true,
                secure: !VITE_DEV,
                sameSite: "strict",
                path: "/api/auth/refersh"
            });
        }
        if (!res.ok)
            return fail(400, {error: await res.text()});
        throw redirect(303, '/db')
    }
}