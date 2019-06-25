package io.pivotalservices.tokenservice;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {JwkFileConroller.class})
public class TestJwkEndpoint {

    @Value("${jwk.filename}")
    private String fileName;

    @Autowired
    private MockMvc mockMvc;

    private static String resourceAsString(String filename) throws IOException, URISyntaxException {
        URI resourceUri = XAuthUserTokenBuilder.class.getClassLoader().getResource(filename).toURI();
        Path path = Paths.get(resourceUri);
        return new String(Files.readAllBytes(path));
    }

    @Test
    public void getCategoriesTest_delegatesToRepositories() throws Exception {
        // Arrange
        String jksFile = resourceAsString(fileName);

        // Act (and Assert)
        mockMvc.perform(get("/jwk"))
                .andDo(print())
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys", hasSize(2)))
                .andExpect(jsonPath("$.keys[0].*", iterableWithSize(10)))
                .andExpect(jsonPath("$.keys[1].*", iterableWithSize(4)))
                .andExpect(jsonPath("$.keys[1]", hasKey("kty")))
                .andExpect(jsonPath("$.keys[1]", hasKey("n")))
                .andExpect(jsonPath("$.keys[1]", hasKey("kid")))
                .andExpect(jsonPath("$.keys[1]", hasKey("e")))
                .andExpect(jsonPath("$.keys[1].n", is("wVTiw2vbOuDnPAQFFkjWnHW2TjNr_W8s4WJgARcGVMv8NDk3bzZafvMA7d1Wy3DSgCHXA0p-gZNeOECkTahE_Y9EtfSm57XAaBwFB6hE6eXu-vc8hNRCGBVb6SX_DirMdlXxK2hKt-MZQlHwISFDdU1sPqdFbBKLrquqrde5Q0ejjYSCQjfWe-kUc9XxfU25QIQ3ohcK3G3jilRwlBP0VQF1D3lqeqd-5_ZvaApII4_XfrRbJG782QwiprW-I7KUMu_2mlPM5MUVMnEANQKKl3ujy9ErjAjtyzYtASoS4oVxr3WuiLBCa78J5HBdxQTWsJ96_MfojP5HESPZp9CAuw")))
                .andExpect(jsonPath("$.keys[1].kty", is("RSA")))
                .andExpect(jsonPath("$.keys[1].e", is("AQAB")))
                .andExpect(content().string(containsString(jksFile)))
        ;
    }
}
