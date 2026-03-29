declare global {
	namespace App {
		interface Locals {
			user: { email: string; role: string } | null;
			accessToken: string | null;
			locations: [ { id: number, name: string, shortName: string } ];
		}
	}
}

export {};
