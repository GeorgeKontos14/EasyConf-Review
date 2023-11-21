# Meeting notes for Flux Meeting in Week 2

**Location**: Flux

**Date**: 21-11-2023

**Attendees**: Georgios Kontos, Rein Lakerveld, Paul Lucas , Chirilă Tudor

**Chair**: Georgios Kontos
**Note taker**: Rein Lakerveld

## Agenda Items

- Check that every team member has access to the GitLab repositories, both for team 05b and team 05 documents.
    - Every team member has acces to the repo's
- Check that every team member has access to the required mattermost channels.
    - Pablo, Tudor and Lucas don't have acces yet to the mattermost channel
- Check that every team member can pass the pipeline.
    - Check
- Check all team members are able to run the example microservice.
    - Everyone can run the example microservice now.

- Database design
    - We had a discussion about the database design. The main points we discussed:
        - How to model the relations and which entities and relations to use.
        - What data will be provided by external microservices and thus won't be stored on our servers.
    - In the end we agreed on a design. Rein will make a digital chart for later reference
- OpenAPI specification
    - One of the main points of discussion was how to authenticate the users. This is needed, because most people can't see a specific review and most people can't see the final verdict on a paper. However, the TA told us not to worry about authentication, thus we decided to just use userIDs to gate access. Depending on your role you will get more or less information about paper or review etc. If you don't have a relationship to a entity, we won't return anything at all.
    - After that, we discussed what methods need to be exposed to external parties. The methods concern 3 broad categories:
        - Papers
        - Reviews
        - Reviewer preferences  

        Besides that, there isn't any information we need to expose to other servers.
    - Today, we made a draft on paper. Tudor will make a proper digital version and upload it to GitLab

    