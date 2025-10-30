import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuthentication } from '../contexts/AuthenticationProvider';

interface ProtectedRouteProps {
    requiredRole?: 'USER' | 'ADMIN';
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
                                                                  requiredRole,
                                                              }) => {
    const { isAuthenticated, user } = useAuthentication();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (requiredRole && user?.role !== requiredRole) {
        if (requiredRole === 'ADMIN' && user?.role === 'USER') {
            return <Navigate to="/404" replace />;
        }

        return <Navigate to="/home" replace />;
    }

    return <Outlet />;
};

export const PublicRoute: React.FC = () => {
    const { isAuthenticated } = useAuthentication();

    if (isAuthenticated) {
        return <Navigate to="/home" replace />;
    }

    return <Outlet />;
};