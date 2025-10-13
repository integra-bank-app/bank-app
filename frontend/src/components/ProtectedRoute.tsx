import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthentication } from '../contexts/AuthenticationProvider';

interface ProtectedRouteProps {
    children: React.ReactNode;
    requiredRole?: 'USER' | 'ADMIN';
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
                                                                  children,
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

    return <>{children}</>;
};

export const PublicRoute: React.FC<{ children: React.ReactNode }> = ({
                                                                         children,
                                                                     }) => {
    const { isAuthenticated } = useAuthentication();

    if (isAuthenticated) {
        return <Navigate to="/home" replace />;
    }

    return <>{children}</>;
};