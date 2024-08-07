/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.refine.ProjectManager;
import com.google.refine.RefineTest;
import com.google.refine.browsing.Engine;
import com.google.refine.browsing.Engine.Mode;
import com.google.refine.browsing.EngineConfig;
import com.google.refine.model.Project;

public class CommandTests extends RefineTest {

    @Override
    @BeforeTest
    public void init() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    CommandStub SUT = null;
    HttpServletRequest request = null;
    ProjectManager projectManager = null;
    Project project = null;

    @BeforeMethod
    public void SetUp() {
        SUT = new CommandStub();
        request = mock(HttpServletRequest.class);
        projectManager = mock(ProjectManager.class);
        project = mock(Project.class);
    }

    @AfterMethod
    public void TearDown() {
        SUT = null;
        request = null;
        projectManager = null;
        project = null;
    }

    // -----------------getProject tests------------

    @Test
    public void getProjectThrowsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetProject(null));
    }

    @Test
    public void getProjectThrowsIfResponseHasNoOrBrokenProjectParameter() {
        when(request.getParameter("project")).thenReturn(""); // null
        assertThrows(ServletException.class, () -> SUT.wrapGetProject(request));
        verify(request, times(1)).getParameter("project");
    }

    // -----------------getEngineConfig tests-----------------
    @Test
    public void getEngineConfigThrowsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetEngineConfig(null));
    }

    @Test
    public void getEngineConfigReturnsNullWithNullEngineParameter() {
        when(request.getParameter("engine")).thenReturn(null);
        Assert.assertNull(SUT.wrapGetEngineConfig(request));
    }

    @Test
    public void getEngineConfigReturnsNullWithEmptyOrBadParameterValue() {
        when(request.getParameter("engine")).thenReturn("sdfasdfas");

        Assert.assertNull(SUT.wrapGetEngineConfig(request));

        verify(request, times(1)).getParameter("engine");
    }

    @Test
    public void getEngineConfigRegressionTest() {
        when(request.getParameter("engine")).thenReturn("{\"mode\":\"row-based\"}");
        EngineConfig o = null;
        o = SUT.wrapGetEngineConfig(request);
        Assert.assertEquals(Mode.RowBased, o.getMode());
        verify(request, times(1)).getParameter("engine");
    }

    // -----------------getEngine tests----------------------
    @Test
    public void getEngineThrowsOnNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetEngine(null, null));

        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetEngine(null, project));

        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetEngine(request, null));
    }

    @Test
    public void getEngineRegressionTest() throws Exception {
        // TODO refactor getEngine to use dependency injection, so a mock Engine
        // object can be used.

        Engine engine = null;
        when(request.getParameter("engine")).thenReturn("{\"hello\":\"world\"}");

        engine = SUT.wrapGetEngine(request, project);
        Assert.assertNotNull(engine);

        verify(request, times(1)).getParameter("engine");
        // JSON configuration doesn't have 'facets' key or 'INCLUDE_DEPENDENT'
        // key, so there should be no further action
        // Engine._facets is protected so can't test that it is of zero length.
    }

    // ------------------
    @Test
    public void getIntegerParameterWithNullParameters() {
        // all null
        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetIntegerParameter(null, null, 0));

        // request null
        assertThrows(IllegalArgumentException.class, () -> SUT.wrapGetIntegerParameter(null, "name", 0));
    }

    @Test
    public void getIntegerParametersWithIncorrectParameterName() {

        when(request.getParameter(null)).thenReturn(null);
        when(request.getParameter("incorrect")).thenReturn(null);

        // name null
        int returned = SUT.wrapGetIntegerParameter(request, null, 5);
        Assert.assertEquals(5, returned);

        // name incorrect
        returned = SUT.wrapGetIntegerParameter(request, "incorrect", 5);
        Assert.assertEquals(5, returned);

        verify(request, times(1)).getParameter(null);
        verify(request, times(1)).getParameter("incorrect");
    }

    @Test
    public void getIntegerParametersRegressionTest() {
        when(request.getParameter("positivenumber")).thenReturn("22");
        when(request.getParameter("zeronumber")).thenReturn("0");
        when(request.getParameter("negativenumber")).thenReturn("-40");

        // positive
        int returned = SUT.wrapGetIntegerParameter(request, "positivenumber", 5);
        Assert.assertEquals(22, returned);

        // zero
        returned = SUT.wrapGetIntegerParameter(request, "zeronumber", 5);
        Assert.assertEquals(0, returned);

        // negative
        returned = SUT.wrapGetIntegerParameter(request,
                "negativenumber", 5);
        Assert.assertEquals(-40, returned);

        verify(request, times(1)).getParameter("positivenumber");
        verify(request, times(1)).getParameter("zeronumber");
        verify(request, times(1)).getParameter("negativenumber");
    }
}
