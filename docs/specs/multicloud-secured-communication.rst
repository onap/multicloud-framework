..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

:orphan:

======================================================
MultiCloud security enhancement: secured communication
======================================================

To support an ONAP Non-Functional Requirement with regarding to Security: "All internal/external system communications shall be able to be encrypted", MultiCloud project needs to explore the best way to implement it.
..
https://wiki.onap.org/display/DW/Casablanca+Release+Requirements#CasablancaReleaseRequirements-NonFunctionalRequirements

Problems Statement
==================

By default all MultiCloud micro-services expose APIs with non-secured endpoints. To fulfill the ONAP security requirement above, either MultiCloud integrate with AAF's CADI SDK or leverage some other technology.
 - Integration with AAF's CADI is preferred by the security subcommittee, however, this requires AAF team or someone provides CADI SDK in python binding. So far there is no promising resource to do that and no roadmap yet.
 - On the other hands, ISTIO's security feature could fulfill this requirement very well without imposing any modification of MultiCloud source code. MSB project team is exploring the way to implement it for OOM based ONAP deployment.


One caveat is that ISTIO approach is only applicable to OOM based ONAP deployment. Hence the question would be:
Whether we should implement this feature for HEAT based ONAP deployment? And if yes, how?

Proposed Solutions
==================

1, **With respect to HEAT based ONAP deployment**:

Given the consensus achieved during ONAP Casablanca Forum, HEAT based ONAP deployment is only for Integration test,
and the fact that many other features are only applicable to OOM based ONAP deployment, I do think it does not hurt to decide
that MultiCloud enable this security feature only for OOM based ONAP deployment.
So the answer to the questions above would be: We will not implement this security feature for HEAT based ONAP deployment

2, **With respect to OOM based ONAP deployment**:

it is intended that MultiCloud project will collaborate with MSB project and VFC project to implement this security feature with the approach of ISTIO.

MultiCloud does not need to change anything, but need to pay attention to following facts:
 - The deployment of the PODs of micro-services: MSB,VFC,MultiCloud will be deployed into seperated kubernetes namespace other than the one for those not utilizing ISTIO features.
 - All communication across different kubernetes namespace should use either IP or FQDN (Full Qualified Domain Name)


Test Use Cases
==================

The pariwise and integration testing will be conducted between VFC and MultiCloud in context of VoLTE or vCPE use case.

