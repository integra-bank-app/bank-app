import { ReactNode } from "react";

type TitleProps = {
    children: ReactNode;
};

export function Title({ children }: TitleProps) {
    return (
        <h2 className={`text-2xl md:text-3xl font-semibold text-gray-900`}>
            {children}
        </h2>
    );
}
