package com.medica.medicamanagement.patient_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class PatientServiceApplicationTests {

//	@Container
//	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16").withDatabaseName("patientservice");
//	@Autowired
//	private MockMvc mockMvc;
//	private final ObjectMapper om = new ObjectMapper();
//	@Autowired
//	private PatientRepo patientRepo;
//
//	// Register PostgreSQL properties dynamically so Spring Boot can use them
//	@DynamicPropertySource
//	static void registerPostgresProperties(DynamicPropertyRegistry registry) {
//		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//	}

	@Test
	void contextLoads() {
		// Test if the container is running
		//assert(postgreSQLContainer.isRunning());
	}

//	@Test
//	void testCreatePatient() throws Exception {
//		PatientRequest patientRequest = getPatientRequest();
//		String patientRequestAsStr = om.writeValueAsString(patientRequest);
//
//		mockMvc.perform(MockMvcRequestBuilders.post("/api/patients/")
//				.contentType(MediaType.APPLICATION_JSON_VALUE)
//				.content(patientRequestAsStr))
//				.andExpect(status().isCreated());
//
//		Assertions.assertEquals(1, patientRepo.findAll().size());
//	}
//
//	private PatientRequest getPatientRequest() {
//		return PatientRequest.builder()
//				.firstName("Amir").lastName("Khan").phone("8999999999").emailId("amirkhan12@gmail.com")
//				.address("Manali, Mumbai").medicalHistory("NA")
//				.build();
//	}
}
