export interface Client {
    id: number;
    name: string;
    email: string;
    phoneNumber?: string;
    cpf?: string;
    dateOfBirth?: string;
    gender?: string;
    role?: string;
    password?: string;
    enabled?: boolean;
    authorities?: { authority: string }[];
    accountNonExpired?: boolean;
    accountNonLocked?: boolean;
    credentialsNonExpired?: boolean;
}
