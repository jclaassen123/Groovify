package com.groovify;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * This class is used for Unit Testing on the Login / Register page. If the back end
 * is down or not working properly and doesn't print "Groovify" to that page, an error
 * is thrown. Otherwise, the build is completed successfully.
 */

@WebMvcTest(com.groovify.GroovifyApplication.class)
public class GroovifyApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void indexTest() throws Exception {
        mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Groovify")));
    }
}
