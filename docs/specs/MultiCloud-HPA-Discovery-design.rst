..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=====================================================
MultiCloud HPA information discovery and registration
=====================================================

To support Beijing Release Use Case: "Hardware Platform Enablement In ONAP" 
(https://wiki.onap.org/display/DW/Hardware+Platform+Enablement+In+ONAP), 
MultiCloud services play a very key role to discover all available Hardware 
Platform capabilities and capacities (referred as HPA: Hardware Platform 
Awareness) from underylying VIMs/Clouds and represent them into AAI, so that 
ONAP OOF service could match the requirement of HPA from VNF vendors as well 
as ONAP policy to the HPA information of underylying VIMs/Clouds to decide 
which VIM/Cloud is the best candidate to place VNFs.


Overall process
================

The trigger to invoke such discovery and representation process will usually 
be ESR (External System Registration service). Whenever the ONAP user is 
onboarding a VIM instance or a cloud, he/she will fill in VIM/Cloud access 
information including but not limited to :  authentication url, user/password,
tenant name, cloud owner and region id, and cloud extra information pertaining
 to this VIM/Cloud. 

Given these access information for a VIM instance, ESR will request MultiCloud 
to perform VIM/Cloud registration process. 

.. [The sequence diagram to illustrate the interactions] https://sequencediagram.org/index.html?initialData=C4S2BsFMAIGFwPYFcAmBaASpA5iBA7aASXzGgBEQBnAYwQDdIAnATwCg2AHAQydBpA98waAFUqzLr36Duw6AFEAyhil8QAoSICC2omplboAWSThQ8ZOgBCTBAGtJPdZrkjT5kJdRoACuCRcfAMNWXksAFsEYEgANSJjaDZxZjQAPmUMAC4xCSZobhQUKjhEHyYcPGDM9N0iHO0i0qs0CtwCaGAEToALGBB8RmEEVjZMgB40NDroHKUkGhpIKioAegAzbhAAiuS8ybRMuYWllY2tncgxlXSPCzKbO0cmHIA5aJB1lgKisCroBDraD4SAAd2gNAerUqBDYdy8UNsDlSaXh3keyJe0CUkCgNGAJWAfWgnACQWgACNuBIUADCAAdfDxYxoYAsTgwQG9bgiSEtNpVOFme4tJHPW7ChEtfyBAY5AAqTBA2GwzGBYIhUIFHUKKD+sLRUJlQXSkWicQSOQA4pAROtwNx6CM1rFjEoQAAvZarEhUYByJaddnLIWedF+MkDA5mmLMnJYKicAgSaCgsA9ArgcBBjklKhIThJviQFChkU+Y0DU2QKKxy3QG0iFC2i4lLmM+2O52rV3ur1rX3+-CneXBkqMqC8eQDEkVeh4JB5mKcMtSiuR-DRmvmuPQBNJ-ApgAUaaJ0Gb-u2VAAlKvw5X8BKw0aN28RhFuOBPTBO06mC63W-AdDyHJZR1zaBGTkWkFF8bRoAGdZ3x5f4Bi6RQ4LQX9nUg-BqWgSAAA8OXxEtKW+AB5V5tF8O8X1lR80jqBomlg7QsIdP8Smg3DqSoBABB5Mi2KwyAeSQCpxzw-BaTAbjgGAJUKSQGISiQGS1UZIkYD5coYWCIA


The VIM/Cloud registration process during which MultiCloud discovers and 
represents HPA information will be implemented in each MultiCloud Plugin 
service respectively which knows best of the corresponding underylying 
VIM/Cloud. 

Proposed alternative solutions to discover HPA information
==========================================================

While it is up to each MultiCloud Plugin service to determine how to discover 
HPA information, there are several alternative solutions to accomplish such 
discovery process.

Solution 1: Hard-coded discovery
--------------------------------

The MultiCloud services are designed in the way that there will be specific 
plugin service to adapting ONAP into specific type or flavor of VIM/Cloud, 
so the specific MultiCloud plugin service is tightly coupled with the 
corresponding VIM/Cloud type or flavor. If some kinds of HPA information are 
statically pertaining to a VIM/Cloud type or flavor and invariant between 
different instance, it will be possible to hard coding this HPA information 
into the corresponding MultiCloud Plugin Service. One example is that Titanium 
Cloud comes along with builtin HPA feature of vswitch with DPDK support, so 
this HPA information will be hard-coded into MultiCloud plugin service for 
Wind River Titanium Cloud.


Solution 2: Manually discovery
------------------------------

There are some kinds of HPA information which vary between instances of the 
same VIM/Cloud type, but will be invariant during the whole life-cycle of 
that instance. These information can be manually injected into ONAP during 
the VIM/Cloud instance onboarding process. There is a field named 
"cloud extra info" from the ESR VIM registration portal, ONAP users could 
input the extra information into ONAP which will be stored into AAI 
(refer to property of "cloud-extra-info" of /cloud-infrastructure/cloud-regions
/cloud-region/{cloud-owner}/{cloud-region-id}). So when MultiCloud Plugin 
Service are invoked to discover HPA information, the cloud extra information 
will be decoded to check if there are HPA information can be extracted and 
represented into AAI.


Solution 3: Automatically discovery 
-----------------------------------

There are some other kinds of HPA information which changes dynamically 
during the life-cycle of a VIM/Cloud instance, so we have to discover them 
leveraging some automation approach. While different VIM/Cloud type of flavor 
exposes different approach to support the automatical discovery with respect 
to HPA resources, the approach is quite straight-forward for OpenStack. 
For those HPA information will be consumed by specifying the extra specs of a 
flavor, VIM/Cloud administrators could provision these flavor's extra specs 
with HPA information before onboarding the VIM/Cloud instance into ONAP. 
After VIM/Cloud onboarding to ONAP, MultiCloud Plugin Service for OpenStack 
will extract these HPA information from the extra specs of the flavors and 
represent them into AAI.


Representation of HPA information
=================================

With regarding to how to represent HPA information into AAI, it is up to how 
AAI schema are defined and we willcontribute and feedback.


Stectch goal
============

There is another consideration to discover new HPA information periodically,
this can be a stretch goal for Beijng Release.

.. [Periodically HPA information discovery] https://sequencediagram.org/index.html?initialData=C4S2BsFMAIAVIE4gPYBMQGNroM4eQG6ICe0yAZtNAKKwCCAtOeAIYHII4BQXADiwlAYQ-AHbBoAWQCu4UAGFwyaagYAhBMgDWiPgKEiW4qbIVKVDWOGkBzEKL2DMh43ToBJRwbESASpABbZGBIADV3SR4uUWCYQkQTORBFZVUNbUQALmgAMQ5oSBYMAAtocMloYGJeSGjYsiIERLNUy2s7UWy8psKSsojoBEg7ZAcZJJSLK1t7BgA+cZap9vts+CQ0TBp6bBA8eIRSGNByTBZQUa5F5PNVaY7oef8gkPLsgHFICWY2DhwAelCFQAyiAAF6QAHuUQ4YBGDAwKo1bjHGBIGzFCQUZo3Vr3VbQADyNRhcIwWn6khw0GkOBgP3YTRCCACABpoAAdUR0MHSIY0unUgDkQOgoIhOCFlUQAU53IA6sCBZDoEL7LD4YjqpApczZVzgMhBpBeEM6cYCAIULToPgArxpCFoDZNNJeDgAHRXUy45YzUQAHgYDGesTe0H8OF4ozp0AA7mBSgy-tAcG7o4JIKh2Vy48UvvmmvljpVCzABDBIDFbKUuWblAgEdS2CwQKwAEZQaD2Uto4YoUTQcj5YDFPZD1iM70TW5tf1PQJhiIfL7YL6t8DU0YFIpJyccQEg8GQf7QjWiBGVbXTpZ3FaB4Oh14DbKR6MwmAJ0druFtlH1dFMTIShrkmO9-WyAARdc-27C9rFQGACHkWAAFUcHZAJAlTY8c1EXAKRwY9oCMVAClEYpNTIrleFYYBhxZIdCmAPkVTTXgMxCMj21IUdzgnX4EBvX1wI6eZQNnfFOmgAA5DgAhYcBj3+YAECMHA6JgLlkwQQ8xWUs84QvLUajlewGIUi5B0NIkZLoWA11OUQszlWhGB06kLPOYSwLnMS5jcdwVwkSAAA89lAUQbG2dz904MhrPzY0RjGH1fKkoMGEC19IXfWMv1KRTwBipg4ubfDexAJpEN-TcfMk+9xLShr-WgYK1zkFhoHbL440gKs5TCiL7GitzSsE8rqNEZy43AUhcHwRoXLtB0QgYF1lHdL0JLxRqAo8TI6A4uaOrhAURugQLLtgdw5VIkjNyNFhjtIDBnqKMBSA1Fit0HXpa1EMadO6lg6TI7dvptcz5POAdoCgARxC9LggA


