/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.workflow.opertion;

import com.webank.wedatasphere.dss.appconn.workflow.utils.Utils;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.DefaultWorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import org.apache.linkis.DataWorkCloudApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowRefCreationOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.DSSJobContentWithContextRequestRef, RefJobContentResponseRef>
        implements RefCreationOperation<ThirdlyRequestRef.DSSJobContentWithContextRequestRef> {

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        //发送RPC请求
        DSSOrchestratorInfo dssOrchestratorInfo = (DSSOrchestratorInfo) requestRef.getDSSJobContent().get(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY);
        String userName = requestRef.getUserName();
        String workflowName = dssOrchestratorInfo.getName();
        String contextId = requestRef.getContextId() != null ? requestRef.getContextId() : "";
        String description = dssOrchestratorInfo.getDesc();
        List<DSSLabel> dssLabels = requestRef.getDSSLabels();
        String orcVersion = (String) requestRef.getDSSJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_VERSION_KEY);
        String schedulerAppConnName = (String) requestRef.getDSSJobContent().get(OrchestratorRefConstant.ORCHESTRATION_SCHEDULER_APP_CONN);
        long parentFlowId = -1L;
        List<String> linkedAppConnNames = dssOrchestratorInfo.getLinkedAppConnNames() != null ? dssOrchestratorInfo.getLinkedAppConnNames() : new ArrayList<>();
        String uses = dssOrchestratorInfo.getUses() != null ? dssOrchestratorInfo.getUses() : "uses";
        logger.info("try to call workflowManager to create workflow: " + workflowName);
        DSSFlow dssFlow;
        try {
            dssFlow = Utils.getDefaultWorkflowManager().createWorkflow(userName, dssOrchestratorInfo.getProjectId(), workflowName, contextId, description,
                    parentFlowId, uses, linkedAppConnNames, dssLabels, orcVersion, schedulerAppConnName);
        } catch (Exception e) {
            throw new DSSRuntimeException(16002, "调用workflowManager创建workflow出现异常！", e);
        }
        Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssFlow.getId(),
                OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY, dssFlow.getFlowJson());
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refJobContent).success();
    }
}
