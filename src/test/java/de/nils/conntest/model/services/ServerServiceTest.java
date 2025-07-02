package de.nils.conntest.model.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ServerServiceTest
{
    @Test
    void test_serverStart()
    {
        ServerService serverService = new ServerService();

        assertFalse(serverService.startServer());
    }
}
