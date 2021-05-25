# Auction

## Goal

A web auction application for an antique items seller. The application allows users to bid on antique items displayed in the site and admin users to set up items
for auction. Product management and auctioning are within the scope of the application;
shopping cart and payment integration are not.

###  THE SOLUTION
The source code can be found here: [https://github.com/rlagoue/auction](https://github.com/rlagoue/auction)
- BACKEND
    - Technologies: 
      - SPRING BOOT
      - JUNIT  
      - GRADLE
    - E2E tested (no unit test done, since the main use case was covered by the e2e tests)      
    - Dockerfile provided

- FRONTEND
    - Technologies: 
      - VUE 3 with 100% Composition API and Typescript
      - TAILWINDCSS 
      - Cypress        
    - TESTS
      - E2E Tested with cypress    
    - Dockerfile provided

#### HOW TO LAUNCH
- move the repository root directory
- execute the command: `docker-compose up`
- open the browser and navigate to (http://localhost/)


#### HOW TO RUN THE TEST
- BACKEND
  - run the gradle task "test": `gradle test`
- FRONTEND  
  - E2E
    - move to test/e2e
    - execute `npm run test`
    
